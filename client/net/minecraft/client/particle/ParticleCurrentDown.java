package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleCurrentDown extends Particle {
   private float field_203083_a;

   protected ParticleCurrentDown(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.func_70536_a(32);
      this.field_70547_e = (int)(Math.random() * 60.0D) + 30;
      this.field_190017_n = false;
      this.field_187129_i = 0.0D;
      this.field_187130_j = -0.05D;
      this.field_187131_k = 0.0D;
      this.func_187115_a(0.02F, 0.02F);
      this.field_70544_f *= this.field_187136_p.nextFloat() * 0.6F + 0.2F;
      this.field_70545_g = 0.002F;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      float var1 = 0.6F;
      this.field_187129_i += (double)(0.6F * MathHelper.func_76134_b(this.field_203083_a));
      this.field_187131_k += (double)(0.6F * MathHelper.func_76126_a(this.field_203083_a));
      this.field_187129_i *= 0.07D;
      this.field_187131_k *= 0.07D;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      if (!this.field_187122_b.func_204610_c(new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h)).func_206884_a(FluidTags.field_206959_a)) {
         this.func_187112_i();
      }

      if (this.field_70546_d++ >= this.field_70547_e || this.field_187132_l) {
         this.func_187112_i();
      }

      this.field_203083_a = (float)((double)this.field_203083_a + 0.08D);
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      @Nullable
      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleCurrentDown(var2, var3, var5, var7);
      }
   }
}
