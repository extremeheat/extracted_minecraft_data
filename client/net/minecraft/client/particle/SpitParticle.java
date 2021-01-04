package net.minecraft.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class SpitParticle extends ExplodeParticle {
   private SpitParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var8, var10, var12, var14);
      this.gravity = 0.5F;
   }

   public void tick() {
      super.tick();
      this.yd -= 0.004D + 0.04D * (double)this.gravity;
   }

   // $FF: synthetic method
   SpitParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14, Object var15) {
      this(var1, var2, var4, var6, var8, var10, var12, var14);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SpitParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
