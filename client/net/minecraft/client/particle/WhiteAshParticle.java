package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;

public class WhiteAshParticle extends BaseAshSmokeParticle {
   private static final int COLOR_RGB24 = 12235202;

   protected WhiteAshParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, SpriteSet var15) {
      super(var1, var2, var4, var6, 0.1F, -0.1F, 0.1F, var8, var10, var12, var14, var15, 0.0F, 20, 0.0125F, false);
      this.rCol = (float)FastColor.ARGB32.red(12235202) / 255.0F;
      this.gCol = (float)FastColor.ARGB32.green(12235202) / 255.0F;
      this.bCol = (float)FastColor.ARGB32.blue(12235202) / 255.0F;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         RandomSource var15 = var2.random;
         double var16 = (double)var15.nextFloat() * -1.9 * (double)var15.nextFloat() * 0.1;
         double var18 = (double)var15.nextFloat() * -0.5 * (double)var15.nextFloat() * 0.1 * 5.0;
         double var20 = (double)var15.nextFloat() * -1.9 * (double)var15.nextFloat() * 0.1;
         return new WhiteAshParticle(var2, var3, var5, var7, var16, var18, var20, 1.0F, this.sprites);
      }

      // $FF: synthetic method
      public Particle createParticle(ParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
