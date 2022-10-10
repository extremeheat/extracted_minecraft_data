package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleSuspend extends Particle {
   protected ParticleSuspend(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4 - 0.125D, var6, var8, var10, var12);
      this.field_70552_h = 0.4F;
      this.field_70553_i = 0.4F;
      this.field_70551_j = 0.7F;
      this.func_70536_a(0);
      this.func_187115_a(0.01F, 0.01F);
      this.field_70544_f *= this.field_187136_p.nextFloat() * 0.6F + 0.2F;
      this.field_187129_i = var8 * 0.0D;
      this.field_187130_j = var10 * 0.0D;
      this.field_187131_k = var12 * 0.0D;
      this.field_70547_e = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      if (!this.field_187122_b.func_204610_c(new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h)).func_206884_a(FluidTags.field_206959_a)) {
         this.func_187112_i();
      }

      if (this.field_70547_e-- <= 0) {
         this.func_187112_i();
      }

   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleSuspend(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
