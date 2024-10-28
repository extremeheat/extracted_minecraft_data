package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class GustSeedParticle extends NoRenderParticle {
   private final double scale;
   private final int tickDelayInBetween;

   GustSeedParticle(ClientLevel var1, double var2, double var4, double var6, double var8, int var10, int var11) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.scale = var8;
      this.lifetime = var10;
      this.tickDelayInBetween = var11;
   }

   public void tick() {
      if (this.age % (this.tickDelayInBetween + 1) == 0) {
         for(int var1 = 0; var1 < 3; ++var1) {
            double var2 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * this.scale;
            double var4 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * this.scale;
            double var6 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * this.scale;
            this.level.addParticle(ParticleTypes.GUST, var2, var4, var6, (double)((float)this.age / (float)this.lifetime), 0.0, 0.0);
         }
      }

      if (this.age++ == this.lifetime) {
         this.remove();
      }

   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final double scale;
      private final int lifetime;
      private final int tickDelayInBetween;

      public Provider(double var1, int var3, int var4) {
         super();
         this.scale = var1;
         this.lifetime = var3;
         this.tickDelayInBetween = var4;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new GustSeedParticle(var2, var3, var5, var7, this.scale, this.lifetime, this.tickDelayInBetween);
      }

      // $FF: synthetic method
      public Particle createParticle(ParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
