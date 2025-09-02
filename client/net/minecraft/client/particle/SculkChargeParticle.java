package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.util.RandomSource;

public class SculkChargeParticle extends SingleQuadParticle {
   private final SpriteSet sprites;

   SculkChargeParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var8, var10, var12, var14.first());
      this.friction = 0.96F;
      this.sprites = var14;
      this.scale(1.5F);
      this.hasPhysics = false;
      this.setSpriteFromAge(var14);
   }

   public int getLightColor(float var1) {
      return 240;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public static record Provider(SpriteSet sprite) implements ParticleProvider<SculkChargeParticleOptions> {
      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SculkChargeParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SculkChargeParticle var16 = new SculkChargeParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         var16.setAlpha(1.0F);
         var16.setParticleSpeed(var9, var11, var13);
         var16.oRoll = var1.roll();
         var16.roll = var1.roll();
         var16.setLifetime(var15.nextInt(12) + 8);
         return var16;
      }
   }
}
