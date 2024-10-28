package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SculkChargeParticleOptions;

public class SculkChargeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   SculkChargeParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.friction = 0.96F;
      this.sprites = var14;
      this.scale(1.5F);
      this.hasPhysics = false;
      this.setSpriteFromAge(var14);
   }

   public int getLightColor(float var1) {
      return 240;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public static record Provider(SpriteSet sprite) implements ParticleProvider<SculkChargeParticleOptions> {
      public Provider(SpriteSet sprite) {
         super();
         this.sprite = sprite;
      }

      public Particle createParticle(SculkChargeParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SculkChargeParticle var15 = new SculkChargeParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         var15.setAlpha(1.0F);
         var15.setParticleSpeed(var9, var11, var13);
         var15.oRoll = var1.roll();
         var15.roll = var1.roll();
         var15.setLifetime(var2.random.nextInt(12) + 8);
         return var15;
      }

      public SpriteSet sprite() {
         return this.sprite;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SculkChargeParticleOptions)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
