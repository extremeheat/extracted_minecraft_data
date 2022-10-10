package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleBubbleColumnUp extends Particle {
   protected ParticleBubbleColumnUp(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.func_70536_a(32);
      this.func_187115_a(0.02F, 0.02F);
      this.field_70544_f *= this.field_187136_p.nextFloat() * 0.6F + 0.2F;
      this.field_187129_i = var8 * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
      this.field_187130_j = var10 * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
      this.field_187131_k = var12 * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
      this.field_70547_e = (int)(40.0D / (Math.random() * 0.8D + 0.2D));
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.field_187130_j += 0.005D;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187129_i *= 0.8500000238418579D;
      this.field_187130_j *= 0.8500000238418579D;
      this.field_187131_k *= 0.8500000238418579D;
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
         return new ParticleBubbleColumnUp(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
