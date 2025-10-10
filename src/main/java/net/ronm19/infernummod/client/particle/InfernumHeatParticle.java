package net.ronm19.infernummod.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

/**
 * 🔥 Subtle heat shimmer particle used by Lava Vision
 */
public class InfernumHeatParticle extends SpriteBillboardParticle {
    protected InfernumHeatParticle( ClientWorld world, double x, double y, double z, SpriteProvider sprites, double vx, double vy, double vz) {
        super(world, x, y, z, vx, vy, vz);
        this.maxAge = 40 + this.random.nextInt(20);
        this.scale = 0.4f + this.random.nextFloat() * 0.3f;
        this.gravityStrength = -0.01f;
        this.alpha = 0.15f + this.random.nextFloat() * 0.1f;
        this.velocityX = vx * 0.02;
        this.velocityY = 0.03 + this.random.nextFloat() * 0.02;
        this.velocityZ = vz * 0.02;
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha *= 0.98f;
        this.scale *= 1.01f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteProvider) {
            this.sprites = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld clientWorld,
                                       double x, double y, double z, double xd, double yd, double zd) {
            return new InfernumHeatParticle(clientWorld, x, y, z, this.sprites, xd, yd, zd);
        }
    }
}