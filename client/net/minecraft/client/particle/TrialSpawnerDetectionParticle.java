package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class TrialSpawnerDetectionParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private static final int BASE_LIFETIME = 8;

   protected TrialSpawnerDetectionParticle(
      ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, SpriteSet var15
   ) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.sprites = var15;
      this.friction = 0.96F;
      this.gravity = -0.1F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.xd *= 0.0;
      this.yd *= 0.9;
      this.zd *= 0.0;
      this.xd += var8;
      this.yd += var10;
      this.zd += var12;
      this.quadSize *= 0.75F * var14;
      this.lifetime = (int)(8.0F / Mth.randomBetween(this.random, 0.5F, 1.0F) * var14);
      this.lifetime = Math.max(this.lifetime, 1);
      this.setSpriteFromAge(var15);
      this.hasPhysics = true;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @Override
   public int getLightColor(float var1) {
      return 240;
   }

   @Override
   public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
      return SingleQuadParticle.FacingCameraMode.LOOKAT_Y;
   }

   @Override
   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   @Override
   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new TrialSpawnerDetectionParticle(var2, var3, var5, var7, var9, var11, var13, 1.5F, this.sprites);
      }
   }
}
