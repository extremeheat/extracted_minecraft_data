package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleFlame extends Particle {
   private final float field_70562_a;

   protected ParticleFlame(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_187129_i = this.field_187129_i * 0.009999999776482582D + var8;
      this.field_187130_j = this.field_187130_j * 0.009999999776482582D + var10;
      this.field_187131_k = this.field_187131_k * 0.009999999776482582D + var12;
      this.field_187126_f += (double)((this.field_187136_p.nextFloat() - this.field_187136_p.nextFloat()) * 0.05F);
      this.field_187127_g += (double)((this.field_187136_p.nextFloat() - this.field_187136_p.nextFloat()) * 0.05F);
      this.field_187128_h += (double)((this.field_187136_p.nextFloat() - this.field_187136_p.nextFloat()) * 0.05F);
      this.field_70562_a = this.field_70544_f;
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.field_70547_e = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
      this.func_70536_a(48);
   }

   public void func_187110_a(double var1, double var3, double var5) {
      this.func_187108_a(this.func_187116_l().func_72317_d(var1, var3, var5));
      this.func_187118_j();
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e;
      this.field_70544_f = this.field_70562_a * (1.0F - var9 * var9 * 0.5F);
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public int func_189214_a(float var1) {
      float var2 = ((float)this.field_70546_d + var1) / (float)this.field_70547_e;
      var2 = MathHelper.func_76131_a(var2, 0.0F, 1.0F);
      int var3 = super.func_189214_a(var1);
      int var4 = var3 & 255;
      int var5 = var3 >> 16 & 255;
      var4 += (int)(var2 * 15.0F * 16.0F);
      if (var4 > 240) {
         var4 = 240;
      }

      return var4 | var5 << 16;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187129_i *= 0.9599999785423279D;
      this.field_187130_j *= 0.9599999785423279D;
      this.field_187131_k *= 0.9599999785423279D;
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
         return new ParticleFlame(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
