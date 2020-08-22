package net.minecraft.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class LargeSmokeParticle extends SmokeParticle {
   protected LargeSmokeParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var8, var10, var12, 2.5F, var14);
   }

   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new LargeSmokeParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
