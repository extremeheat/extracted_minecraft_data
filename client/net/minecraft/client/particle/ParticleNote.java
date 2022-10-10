package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleNote extends Particle {
   private final float field_70585_a;

   protected ParticleNote(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6, var8, var10, var12, 2.0F);
   }

   protected ParticleNote(World var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_187129_i *= 0.009999999776482582D;
      this.field_187130_j *= 0.009999999776482582D;
      this.field_187131_k *= 0.009999999776482582D;
      this.field_187130_j += 0.2D;
      this.field_70552_h = MathHelper.func_76126_a(((float)var8 + 0.0F) * 6.2831855F) * 0.65F + 0.35F;
      this.field_70553_i = MathHelper.func_76126_a(((float)var8 + 0.33333334F) * 6.2831855F) * 0.65F + 0.35F;
      this.field_70551_j = MathHelper.func_76126_a(((float)var8 + 0.6666667F) * 6.2831855F) * 0.65F + 0.35F;
      this.field_70544_f *= 0.75F;
      this.field_70544_f *= var14;
      this.field_70585_a = this.field_70544_f;
      this.field_70547_e = 6;
      this.func_70536_a(64);
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e * 32.0F;
      var9 = MathHelper.func_76131_a(var9, 0.0F, 1.0F);
      this.field_70544_f = this.field_70585_a * var9;
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      if (this.field_187127_g == this.field_187124_d) {
         this.field_187129_i *= 1.1D;
         this.field_187131_k *= 1.1D;
      }

      this.field_187129_i *= 0.6600000262260437D;
      this.field_187130_j *= 0.6600000262260437D;
      this.field_187131_k *= 0.6600000262260437D;
      if (this.field_187132_l) {
         this.field_187129_i *= 0.699999988079071D;
         this.field_187131_k *= 0.699999988079071D;
      }

   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleNote(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
