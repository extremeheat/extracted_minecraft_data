package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class ParticleSplash extends ParticleRain {
   protected ParticleSplash(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6);
      this.field_70545_g = 0.04F;
      this.func_70536_a(20 + this.field_187136_p.nextInt(3));
      if (var10 == 0.0D && (var8 != 0.0D || var12 != 0.0D)) {
         this.field_187129_i = var8;
         this.field_187130_j = 0.1D;
         this.field_187131_k = var12;
      }

   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleSplash(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
