package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleRain extends Particle {
   protected ParticleRain(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_187129_i *= 0.30000001192092896D;
      this.field_187130_j = Math.random() * 0.20000000298023224D + 0.10000000149011612D;
      this.field_187131_k *= 0.30000001192092896D;
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.func_70536_a(19 + this.field_187136_p.nextInt(4));
      this.func_187115_a(0.01F, 0.01F);
      this.field_70545_g = 0.06F;
      this.field_70547_e = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.field_187130_j -= (double)this.field_70545_g;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187129_i *= 0.9800000190734863D;
      this.field_187130_j *= 0.9800000190734863D;
      this.field_187131_k *= 0.9800000190734863D;
      if (this.field_70547_e-- <= 0) {
         this.func_187112_i();
      }

      if (this.field_187132_l) {
         if (Math.random() < 0.5D) {
            this.func_187112_i();
         }

         this.field_187129_i *= 0.699999988079071D;
         this.field_187131_k *= 0.699999988079071D;
      }

      BlockPos var1 = new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h);
      IBlockState var2 = this.field_187122_b.func_180495_p(var1);
      Material var3 = var2.func_185904_a();
      IFluidState var4 = this.field_187122_b.func_204610_c(var1);
      if (!var4.func_206888_e() || var3.func_76220_a()) {
         double var5;
         if (var4.func_206885_f() > 0.0F) {
            var5 = (double)var4.func_206885_f();
         } else {
            var5 = var2.func_196952_d(this.field_187122_b, var1).func_197760_b(EnumFacing.Axis.Y, this.field_187126_f - Math.floor(this.field_187126_f), this.field_187128_h - Math.floor(this.field_187128_h));
         }

         double var7 = (double)MathHelper.func_76128_c(this.field_187127_g) + var5;
         if (this.field_187127_g < var7) {
            this.func_187112_i();
         }
      }

   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleRain(var2, var3, var5, var7);
      }
   }
}
