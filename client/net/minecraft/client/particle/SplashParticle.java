package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class SplashParticle extends WaterDropParticle {
   private SplashParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6);
      this.gravity = 0.04F;
      if (var10 == 0.0D && (var8 != 0.0D || var12 != 0.0D)) {
         this.xd = var8;
         this.yd = 0.1D;
         this.zd = var12;
      }

   }

   // $FF: synthetic method
   SplashParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, Object var14) {
      this(var1, var2, var4, var6, var8, var10, var12);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SplashParticle var15 = new SplashParticle(var2, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
