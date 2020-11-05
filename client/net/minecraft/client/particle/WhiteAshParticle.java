package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class WhiteAshParticle extends BaseAshSmokeParticle {
   protected WhiteAshParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, SpriteSet var15) {
      super(var1, var2, var4, var6, 0.1F, -0.1F, 0.1F, var8, var10, var12, var14, var15, 0.0F, 20, -5.0E-4D, false);
      this.rCol = 0.7294118F;
      this.gCol = 0.69411767F;
      this.bCol = 0.7607843F;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         Random var15 = var2.random;
         double var16 = (double)var15.nextFloat() * -1.9D * (double)var15.nextFloat() * 0.1D;
         double var18 = (double)var15.nextFloat() * -0.5D * (double)var15.nextFloat() * 0.1D * 5.0D;
         double var20 = (double)var15.nextFloat() * -1.9D * (double)var15.nextFloat() * 0.1D;
         return new WhiteAshParticle(var2, var3, var5, var7, var16, var18, var20, 1.0F, this.sprites);
      }
   }
}
