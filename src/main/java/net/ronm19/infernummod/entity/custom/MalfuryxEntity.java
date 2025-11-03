package net.ronm19.infernummod.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ronm19.infernummod.api.interfaces.AttackingEntity;

import java.util.*;

public class MalfuryxEntity extends HostileEntity implements AttackingEntity {

    // ---------------- CONFIG ----------------
    private static final double SCAN_RADIUS = 24.0;
    private static final int SCAN_INTERVAL_TICKS = 100;
    private static final int FORGIVENESS_TICKS = 6000;
    private static final int SIN_INVENTORY_WEIGHT = 5;
    private static final int SIN_THRESHOLD_WATCH = 10;
    private static final int SIN_THRESHOLD_JUDGE = 30;

    // ---------------- DATA ----------------
    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(MalfuryxEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    private int idleAnimationTimeout;
    private int attackAnimationTimeout;

    private final Map<UUID, Integer> forgiven = new HashMap<>();
    private final Map<UUID, Integer> sinScores = new HashMap<>();
    private int scanTicker = this.random.nextBetween(0, SCAN_INTERVAL_TICKS);

    private boolean dictatorMode = false;

    // ---------------- ILLEGAL / SACRED ----------------
    private static Identifier id(String s) { return Identifier.tryParse(s); }

    private static final Set<Identifier> FORBIDDEN_ITEMS = Set.of(
            id("minecraft:nether_star"),
            id("minecraft:blaze_rod"),
            id("minecraft:ghast_tear"),
            id("minecraft:wither_skeleton_skull")
    );

    private static final Set<Identifier> ILLEGAL_TOOLS = Set.of(
            id("infernummod:infernal_staff"),
            id("infernummod:abyssal_blade"),
            id("infernummod:hell_crown")
    );

    private static final Set<Identifier> SACRED_BLOCKS = Set.of(
            id("infernummod:abyssium_stone"),
            id("infernummod:infernal_obsidian"),
            id("infernummod:infernal_ice_statue"),
            id("infernummod:infernal_eye_statue_block"),
            id("minecraft:beacon"),
            id("minecraft:enchanting_table")
    );

    // ---------------- MOCKERY ----------------
    private static final String[] MOCKERY_LINES = {
            "You call that strength?",
            "Your crimes are your crown, mortal.",
            "Kneel before the law of flame!",
            "Weak… pathetic… sinful.",
            "You thought you could defy Malfuryx?"
    };

    // ---------------- CONSTRUCTOR ----------------
    public MalfuryxEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        this.experiencePoints = 20;
    }

    // ---------------- ATTRIBUTES ----------------
    public static DefaultAttributeContainer.Builder createMalfuryxAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.36D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8D);
    }

    // ---------------- GOALS ----------------
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.25D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false,
                target -> target instanceof PlayerEntity p && shouldJudge(p)));
    }

    // ---------------- CORE LOGIC ----------------
    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            runClientAnimations();
            return;
        }

        // Forgiveness timer
        if (!forgiven.isEmpty()) {
            forgiven.replaceAll((id, t) -> t - 1);
            forgiven.entrySet().removeIf(e -> e.getValue() <= 0);
        }

        // Scan cycle
        if (--scanTicker <= 0) {
            scanTicker = SCAN_INTERVAL_TICKS;
            performWatcherScan();
        }

        boolean attacking = this.getTarget() instanceof PlayerEntity p && shouldJudge(p);
        setAttacking(attacking);

        // Dictator Mode trigger
        if (!dictatorMode && (this.getHealth() <= this.getMaxHealth() * 0.3F ||
                (this.getTarget() instanceof PlayerEntity p && getSin(p) >= 60))) {
            enterDictatorMode();
        }
    }

    private void performWatcherScan() {
        if (!(this.getWorld() instanceof ServerWorld server)) return;

        List<PlayerEntity> players = server.getEntitiesByClass(
                PlayerEntity.class,
                new Box(getX() - SCAN_RADIUS, getY() - 8, getZ() - SCAN_RADIUS,
                        getX() + SCAN_RADIUS, getY() + 8, getZ() + SCAN_RADIUS),
                p -> !p.isCreative() && !p.isSpectator()
        );

        for (PlayerEntity p : players) {
            int sin = computeSin(p);
            sinScores.put(p.getUuid(), sin);

            if (sin >= SIN_THRESHOLD_WATCH) {
                applySinAura(p, sin);
                spawnWatcherParticlesNear(p, sin);
            }

            if (isNearInfernalEye(p)) mockPlayer(p);
        }

        // Targeting update
        if (this.getTarget() instanceof PlayerEntity pt && !shouldJudge(pt)) {
            this.setTarget(null);
        } else {
            PlayerEntity worst = players.stream()
                    .filter(this::shouldJudge)
                    .max(Comparator.comparingInt(this::getSin))
                    .orElse(null);
            if (worst != null) this.setTarget(worst);
        }
    }

    private int computeSin(PlayerEntity p) {
        if (forgiven.containsKey(p.getUuid())) return 0;

        int sin = 0;

        // Forbidden items
        for (ItemStack st : p.getInventory().main) {
            if (isForbidden(st)) sin += SIN_INVENTORY_WEIGHT;
            if (isIllegalTool(st)) sin += 8;
        }
        for (Hand h : Hand.values()) {
            ItemStack st = p.getStackInHand(h);
            if (isForbidden(st)) sin += SIN_INVENTORY_WEIGHT;
            if (isIllegalTool(st)) sin += 8;
        }

        // Sacred block violation
        if (isBreakingSacredBlock(p)) {
            sin += 15;
            mockPlayer(p);
        }

        // Hell Crown passive pride
        if (hasHellCrownEquipped(p)) sin += 8;

        return sin;
    }

    private boolean isBreakingSacredBlock(PlayerEntity p) {
        World w = p.getWorld();
        BlockPos pos = p.getBlockPos();
        for (BlockPos check : BlockPos.iterateOutwards(pos, 3, 3, 3)) {
            Identifier id = Registries.BLOCK.getId(w.getBlockState(check).getBlock());
            if (SACRED_BLOCKS.contains(id) && w.random.nextFloat() < 0.25F) return true;
        }
        return false;
    }

    private boolean hasHellCrownEquipped(PlayerEntity p) {
        ItemStack head = p.getEquippedStack(EquipmentSlot.HEAD);
        Identifier id = Registries.ITEM.getId(head.getItem());
        return id != null && id.getPath().equals("hell_crown");
    }

    private boolean shouldJudge(PlayerEntity player) {
        return !player.isCreative() && !player.isSpectator() && !forgiven.containsKey(player.getUuid())
                && getSin(player) >= SIN_THRESHOLD_JUDGE;
    }

    private int getSin(PlayerEntity p) {
        return sinScores.getOrDefault(p.getUuid(), 0);
    }

    // ---------------- DICTATOR MODE ----------------
    private void enterDictatorMode() {
        dictatorMode = true;
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 999999, 1));
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 999999, 1));
        this.getWorld().playSound(null, this.getBlockPos(),
                SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 2f, 0.8f);
        if (this.getWorld() instanceof ServerWorld s) {
            s.spawnParticles(ParticleTypes.FLAME, getX(), getBodyY(0.5), getZ(), 50, 2, 1, 2, 0.02);
            s.spawnParticles(ParticleTypes.SMOKE, getX(), getBodyY(0.5), getZ(), 30, 1, 1, 1, 0.01);
        }
    }

    // ---------------- EFFECTS ----------------
    private void applySinAura(PlayerEntity p, int sin) {
        int amp = sin >= SIN_THRESHOLD_JUDGE ? 1 : 0;
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, amp, true, true));
        p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, amp, true, true));
        if (sin < SIN_THRESHOLD_JUDGE)
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0, true, true));
    }

    private void spawnWatcherParticlesNear(PlayerEntity p, int sin) {
        if (!(this.getWorld() instanceof ServerWorld s)) return;
        int count = sin >= SIN_THRESHOLD_JUDGE ? 12 : 6;
        s.spawnParticles(ParticleTypes.SMOKE, p.getX(), p.getBodyY(0.6), p.getZ(), count, 0.3, 0.4, 0.3, 0.01);
        s.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, p.getX(), p.getBodyY(0.6), p.getZ(), count / 3, 0.2, 0.3, 0.2, 0.005);
    }

    // ---------------- ITEM CHECKS ----------------
    private static boolean isForbidden(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return FORBIDDEN_ITEMS.contains(id);
    }

    private static boolean isIllegalTool(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return ILLEGAL_TOOLS.contains(id);
    }

    // ---------------- FORGIVENESS ----------------
    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        Identifier id = Registries.ITEM.getId(stack.getItem());
        if (id != null && (id.getPath().equals("nether_ruby") ||
                id.getPath().equals("diamond") ||
                id.getPath().equals("netherite_ingot"))) {
            forgiven.put(player.getUuid(), FORGIVENESS_TICKS);
            sinScores.put(player.getUuid(), 0);
            if (!player.isCreative()) stack.decrement(1);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 0));
            this.getWorld().playSound(null, this.getBlockPos(),
                    SoundEvents.ENTITY_VILLAGER_YES, SoundCategory.HOSTILE, 1f, 1f);
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    // ---------------- MOCKERY ----------------
    private void mockPlayer(PlayerEntity p) {
        if (!(this.getWorld() instanceof ServerWorld s)) return;
        String msg = isNearInfernalEye(p)
                ? "You dare touch the Eye of Fire? Foolish mortal!"
                : MOCKERY_LINES[s.random.nextInt(MOCKERY_LINES.length)];
        p.sendMessage(Text.literal("§4[Malfuryx]: §c" + msg), false);
    }

    private boolean isNearInfernalEye(PlayerEntity p) {
        World w = p.getWorld();
        for (BlockPos check : BlockPos.iterateOutwards(p.getBlockPos(), 3, 3, 3)) {
            Identifier id = Registries.BLOCK.getId(w.getBlockState(check).getBlock());
            if (id != null && id.getPath().equals("infernal_eye_statue_block")) return true;
        }
        return false;
    }

    // ---------------- COMBAT ----------------
    @Override
    public void attack(LivingEntity target) {
        float baseDamage = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (dictatorMode) baseDamage += 4;
        target.damage(this.getDamageSources().mobAttack(this), baseDamage);
        target.setOnFireFor(3);
        if (this.getWorld() instanceof ServerWorld s) {
            s.spawnParticles(ParticleTypes.FLAME, target.getX(), target.getBodyY(0.5), target.getZ(), 10, 0.35, 0.35, 0.35, 0.01);
            s.spawnParticles(ParticleTypes.SMOKE, target.getX(), target.getBodyY(0.5), target.getZ(), 8, 0.25, 0.25, 0.25, 0.01);
        }
    }

    // ---------------- ANIMATIONS ----------------
    private void runClientAnimations() {
        if (--idleAnimationTimeout <= 0) {
            idleAnimationTimeout = this.random.nextInt(40) + 80;
            idleAnimationState.start(this.age);
        }
        if (isAttacking()) {
            if (--attackAnimationTimeout <= 0) {
                attackAnimationTimeout = 40;
                attackAnimationState.start(this.age);
            }
        } else {
            attackAnimationState.stop();
        }
    }

    // ---------------- SOUNDS ----------------
    @Override public boolean isFireImmune() { return true;}
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_WITHER_AMBIENT; }
    @Override protected SoundEvent getHurtSound( DamageSource source) { return SoundEvents.ENTITY_WITHER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_WITHER_DEATH; }

    @Override public boolean cannotDespawn() {
        return this.getWorld().getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override public boolean isAttacking() { return this.dataTracker.get(ATTACKING); }
    @Override public void setAttacking(boolean attacking) { this.dataTracker.set(ATTACKING, attacking); }

    // ---------------- SAVE / LOAD ----------------
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        NbtList list = new NbtList();
        for (Map.Entry<UUID, Integer> e : forgiven.entrySet()) {
            NbtCompound tag = new NbtCompound();
            tag.putUuid("Id", e.getKey());
            tag.putInt("Ticks", e.getValue());
            list.add(tag);
        }
        nbt.put("Forgiven", list);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        forgiven.clear();
        if (nbt.contains("Forgiven", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("Forgiven", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound tag = list.getCompound(i);
                UUID id = tag.getUuid("Id");
                int ticks = tag.getInt("Ticks");
                if (id != null && ticks > 0) forgiven.put(id, ticks);
            }
        }
    }
}
