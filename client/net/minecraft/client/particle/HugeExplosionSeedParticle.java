package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class HugeExplosionSeedParticle extends NoRenderParticle {
   HugeExplosionSeedParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.lifetime = 8;
   }

   public void tick() {
      for(int var1 = 0; var1 < 6; ++var1) {
         double var2 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
         double var4 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
         double var6 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
         this.level.addParticle(ParticleTypes.EXPLOSION, var2, var4, var6, (double)((float)this.age / (float)this.lifetime), 0.0, 0.0);
      }

      ++this.age;
      if (this.age == this.lifetime) {
         this.remove();
      }

   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider() {
         super();
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new HugeExplosionSeedParticle(var2, var3, var5, var7);
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
