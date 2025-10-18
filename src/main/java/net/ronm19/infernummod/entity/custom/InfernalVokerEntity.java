package net.ronm19.infernummod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.ronm19.infernummod.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfernalVokerEntity extends SpellcastingIllagerEntity {
    @Nullable
    private SheepEntity wololoTarget;

    private final ServerBossBar bossBar =
            new ServerBossBar(this.getDisplayName(), BossBar.Color.RED, BossBar.Style.NOTCHED_10);

    public InfernalVokerEntity(EntityType<? extends SpellcastingIllagerEntity> type, World world) {
        super(type, world);
        this.experiencePoints = 40;
        this.ignoreCameraFrustum = true;
        this.setCustomNameVisible(true);
    }

    // ---------------- ATTRIBUTES ----------------
    public static DefaultAttributeContainer.Builder createInfernalVokerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 120.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.45D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0D);
    }

    // ---------------- GOALS ----------------
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtTargetOrWololoTarget(this));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 8.0F, 0.6, 1.0));
        this.goalSelector.add(3, new InfernalSummonVexGoal(this));
        this.goalSelector.add(4, new LavaBurstSpellGoal(this));
        this.goalSelector.add(5, new WololoGoal(this));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));

        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, IronGolemEntity.class, false));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, TameableEntity.class, false));
    }

    @Override
    public void addBonusForWave( int wave, boolean unused ) {

    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_VINDICATOR_CELEBRATE;
    }

    // ---------------- AI ----------------
    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            // Fire aura particles
            if (this.age % 10 == 0) {
                ((ServerWorld) this.getWorld()).spawnParticles(
                        ParticleTypes.FLAME,
                        this.getX(),
                        this.getY() + 1.5,
                        this.getZ(),
                        6,
                        0.5, 0.2, 0.5,
                        0.01
                );
            }
        }
    }

    @Override
    protected SoundEvent getCastSpellSound() {
        return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
    }

    // ---------------- SPELL: Lava Burst ----------------
    private void castLavaBurst() {
        World world = this.getWorld();
        BlockPos pos = this.getBlockPos();

        world.playSound(null, pos, SoundEvents.ENTITY_BLAZE_SHOOT, this.getSoundCategory(), 1.2F, 1.0F);

        if (world instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 12; i++) {
                double dx = pos.getX() + (random.nextDouble() - 0.5D) * 6.0D;
                double dy = pos.getY() + 0.2D;
                double dz = pos.getZ() + (random.nextDouble() - 0.5D) * 6.0D;
                serverWorld.spawnParticles(ParticleTypes.LAVA, dx, dy, dz, 3, 0, 0.1, 0, 0.05);
            }
        }

        // Damage players in range
        this.getWorld().getEntitiesByClass(PlayerEntity.class, this.getBoundingBox().expand(5.0D),
                player -> player.isAlive()).forEach(player -> {
            player.setOnFireFor(5);
            player.damage(this.getDamageSources().magic(), 8.0F);
        });
    }

    // ---------------- SPELL: Infernal Vex Summon ----------------
    private void summonInfernalVexes() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        int count = 3 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            InfernalVexEntity vex = ModEntities.INFERNAL_VEX.create(serverWorld);
            if (vex != null) {
                vex.refreshPositionAndAngles(this.getX() + random.nextBetween(-3, 3),
                        this.getY() + 1, this.getZ() + random.nextBetween(-3, 3), 0, 0);
                vex.setOwner(this);
                vex.setCustomNameVisible(true);
                vex.setCustomName(this.getDisplayName().copy().append("’s Infernal Vex"));
                vex.setFireTicks(2000);
                vex.setGlowing(true);
                serverWorld.spawnEntity(vex);
            }
        }
    }

    // ---------------- BOSS BAR ----------------
    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        bossBar.removePlayer(player);
    }

    // ---------------- SOUNDS ----------------
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_EVOKER_HURT;
    }

    // ---------------- GOAL CLASSES ----------------

    class LookAtTargetOrWololoTarget extends SpellcastingIllagerEntity.LookAtTargetGoal {
        LookAtTargetOrWololoTarget(InfernalVokerEntity infernalVokerEntity) { super(); }

        public void tick() {
            if (InfernalVokerEntity.this.getTarget() != null) {
                InfernalVokerEntity.this.getLookControl().lookAt(InfernalVokerEntity.this.getTarget(),
                        (float) InfernalVokerEntity.this.getMaxHeadRotation(),
                        (float) InfernalVokerEntity.this.getMaxLookPitchChange());
            } else if (InfernalVokerEntity.this.getWololoTarget() != null) {
                InfernalVokerEntity.this.getLookControl().lookAt(InfernalVokerEntity.this.getWololoTarget(),
                        (float) InfernalVokerEntity.this.getMaxHeadRotation(),
                        (float) InfernalVokerEntity.this.getMaxLookPitchChange());
            }
        }
    }

    // Custom Lava Burst Spell Goal
    class LavaBurstSpellGoal extends SpellcastingIllagerEntity.CastSpellGoal {
        LavaBurstSpellGoal(InfernalVokerEntity infernalVokerEntity) {
            super();
        }

        protected int getSpellTicks() { return 60; }
        protected int startTimeDelay() { return 160; }

        protected void castSpell() {
            castLavaBurst();
        }

        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_BLAZE_SHOOT;
        }

        protected Spell getSpell() {
            return Spell.FANGS; // reusing default spell ID
        }
    }

    // Summon Infernal Vexes instead of normal ones
    class InfernalSummonVexGoal extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate closeVexPredicate =
                TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0).ignoreVisibility().ignoreDistanceScalingFactor();

        InfernalSummonVexGoal(InfernalVokerEntity infernalVokerEntity) {
            super();
        }

        public boolean canStart() {
            if (!super.canStart()) return false;
            int i = InfernalVokerEntity.this.getWorld().getTargets(VexEntity.class, this.closeVexPredicate,
                    InfernalVokerEntity.this, InfernalVokerEntity.this.getBoundingBox().expand(16.0)).size();
            return InfernalVokerEntity.this.random.nextInt(8) + 1 > i;
        }

        protected int getSpellTicks() { return 100; }
        protected int startTimeDelay() { return 340; }

        protected void castSpell() { summonInfernalVexes(); }

        protected SoundEvent getSoundPrepare() { return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON; }

        protected Spell getSpell() { return Spell.SUMMON_VEX; }
    }

    // Keeps the “Wololo” goal for fun, color flames red
    public class WololoGoal extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate convertibleSheepPredicate =
                TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0)
                        .setPredicate(livingEntity -> ((SheepEntity) livingEntity).getColor() == DyeColor.BLUE);

        public WololoGoal(InfernalVokerEntity infernalVokerEntity) { super(); }

        public boolean canStart() {
            if (InfernalVokerEntity.this.getTarget() != null) return false;
            if (InfernalVokerEntity.this.isSpellcasting()) return false;
            if (InfernalVokerEntity.this.age < this.startTime) return false;
            if (!InfernalVokerEntity.this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING))
                return false;

            List<SheepEntity> list = InfernalVokerEntity.this.getWorld().getTargets(
                    SheepEntity.class, this.convertibleSheepPredicate, InfernalVokerEntity.this,
                    InfernalVokerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0));
            if (list.isEmpty()) return false;

            InfernalVokerEntity.this.setWololoTarget(list.get(InfernalVokerEntity.this.random.nextInt(list.size())));
            return true;
        }

        public boolean shouldContinue() {
            return InfernalVokerEntity.this.getWololoTarget() != null && this.spellCooldown > 0;
        }

        public void stop() {
            super.stop();
            InfernalVokerEntity.this.setWololoTarget(null);
        }

        protected void castSpell() {
            SheepEntity sheep = InfernalVokerEntity.this.getWololoTarget();
            if (sheep != null && sheep.isAlive()) {
                sheep.setColor(DyeColor.RED);
            }
        }

        protected int getInitialCooldown() { return 40; }
        protected int getSpellTicks() { return 60; }
        protected int startTimeDelay() { return 140; }
        protected SoundEvent getSoundPrepare() { return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO; }
        protected Spell getSpell() { return Spell.WOLOLO; }
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    // ---------------- ACCESSORS ----------------
    public void setWololoTarget(@Nullable SheepEntity target) { this.wololoTarget = target; }
    @Nullable public SheepEntity getWololoTarget() { return this.wololoTarget; }
}