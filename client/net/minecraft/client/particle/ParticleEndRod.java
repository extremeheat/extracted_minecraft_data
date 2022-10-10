package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class ParticleEndRod extends ParticleSimpleAnimated {
   public ParticleEndRod(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 176, 8, -5.0E-4F);
      this.field_187129_i = var8;
      this.field_187130_j = var10;
      this.field_187131_k = var12;
      this.field_70544_f *= 0.75F;
      this.field_70547_e = 60 + this.field_187136_p.nextInt(12);
      this.func_187145_d(15916745);
   }

   public void func_187110_a(double var1, double var3, double var5) {
      this.func_187108_a(this.func_187116_l().func_72317_d(var1, var3, var5));
      this.func_187118_j();
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleEndRod(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
