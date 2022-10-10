package net.minecraft.client.particle;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.init.Particles;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleDrip extends Particle {
   private final Fluid field_204502_a;
   private int field_70564_aq;

   protected ParticleDrip(World var1, double var2, double var4, double var6, Fluid var8) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_187129_i = 0.0D;
      this.field_187130_j = 0.0D;
      this.field_187131_k = 0.0D;
      if (var8.func_207185_a(FluidTags.field_206959_a)) {
         this.field_70552_h = 0.0F;
         this.field_70553_i = 0.0F;
         this.field_70551_j = 1.0F;
      } else {
         this.field_70552_h = 1.0F;
         this.field_70553_i = 0.0F;
         this.field_70551_j = 0.0F;
      }

      this.func_70536_a(113);
      this.func_187115_a(0.01F, 0.01F);
      this.field_70545_g = 0.06F;
      this.field_204502_a = var8;
      this.field_70564_aq = 40;
      this.field_70547_e = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
      this.field_187129_i = 0.0D;
      this.field_187130_j = 0.0D;
      this.field_187131_k = 0.0D;
   }

   public int func_189214_a(float var1) {
      return this.field_204502_a.func_207185_a(FluidTags.field_206959_a) ? super.func_189214_a(var1) : 257;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_204502_a.func_207185_a(FluidTags.field_206959_a)) {
         this.field_70552_h = 0.2F;
         this.field_70553_i = 0.3F;
         this.field_70551_j = 1.0F;
      } else {
         this.field_70552_h = 1.0F;
         this.field_70553_i = 16.0F / (float)(40 - this.field_70564_aq + 16);
         this.field_70551_j = 4.0F / (float)(40 - this.field_70564_aq + 8);
      }

      this.field_187130_j -= (double)this.field_70545_g;
      if (this.field_70564_aq-- > 0) {
         this.field_187129_i *= 0.02D;
         this.field_187130_j *= 0.02D;
         this.field_187131_k *= 0.02D;
         this.func_70536_a(113);
      } else {
         this.func_70536_a(112);
      }

      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187129_i *= 0.9800000190734863D;
      this.field_187130_j *= 0.9800000190734863D;
      this.field_187131_k *= 0.9800000190734863D;
      if (this.field_70547_e-- <= 0) {
         this.func_187112_i();
      }

      if (this.field_187132_l) {
         if (this.field_204502_a.func_207185_a(FluidTags.field_206959_a)) {
            this.func_187112_i();
            this.field_187122_b.func_195594_a(Particles.field_197606_Q, this.field_187126_f, this.field_187127_g, this.field_187128_h, 0.0D, 0.0D, 0.0D);
         } else {
            this.func_70536_a(114);
         }

         this.field_187129_i *= 0.699999988079071D;
         this.field_187131_k *= 0.699999988079071D;
      }

      BlockPos var1 = new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h);
      IFluidState var2 = this.field_187122_b.func_204610_c(var1);
      if (var2.func_206886_c() == this.field_204502_a) {
         double var3 = (double)((float)MathHelper.func_76128_c(this.field_187127_g) + var2.func_206885_f());
         if (this.field_187127_g < var3) {
            this.func_187112_i();
         }
      }

   }

   public static class LavaFactory implements IParticleFactory<BasicParticleType> {
      public LavaFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleDrip(var2, var3, var5, var7, Fluids.field_204547_b);
      }
   }

   public static class WaterFactory implements IParticleFactory<BasicParticleType> {
      public WaterFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleDrip(var2, var3, var5, var7, Fluids.field_204546_a);
      }
   }
}
