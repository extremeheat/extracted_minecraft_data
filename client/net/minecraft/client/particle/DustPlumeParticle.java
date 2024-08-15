package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.ARGB;

public class DustPlumeParticle extends BaseAshSmokeParticle {
   private static final int COLOR_RGB24 = 12235202;

   protected DustPlumeParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, SpriteSet var15) {
      super(var1, var2, var4, var6, 0.7F, 0.6F, 0.7F, var8, var10 + 0.15000000596046448, var12, var14, var15, 0.5F, 7, 0.5F, false);
      float var16 = (float)Math.random() * 0.2F;
      this.rCol = (float)ARGB.red(12235202) / 255.0F - var16;
      this.gCol = (float)ARGB.green(12235202) / 255.0F - var16;
      this.bCol = (float)ARGB.blue(12235202) / 255.0F - var16;
   }

   @Override
   public void tick() {
      this.gravity = 0.88F * this.gravity;
      this.friction = 0.92F * this.friction;
      super.tick();
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new DustPlumeParticle(var2, var3, var5, var7, var9, var11, var13, 1.0F, this.sprites);
      }
   }
}
