package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class ParticleEnchantmentTable extends Particle {
   private final double field_70568_aq;
   private final double field_70567_ar;
   private final double field_70566_as;

   protected ParticleEnchantmentTable(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_187129_i = var8;
      this.field_187130_j = var10;
      this.field_187131_k = var12;
      this.field_70568_aq = var2;
      this.field_70567_ar = var4;
      this.field_70566_as = var6;
      this.field_187123_c = var2 + var8;
      this.field_187124_d = var4 + var10;
      this.field_187125_e = var6 + var12;
      this.field_187126_f = this.field_187123_c;
      this.field_187127_g = this.field_187124_d;
      this.field_187128_h = this.field_187125_e;
      float var14 = this.field_187136_p.nextFloat() * 0.6F + 0.4F;
      this.field_70544_f = this.field_187136_p.nextFloat() * 0.5F + 0.2F;
      this.field_70552_h = 0.9F * var14;
      this.field_70553_i = 0.9F * var14;
      this.field_70551_j = var14;
      this.field_190017_n = false;
      this.field_70547_e = (int)(Math.random() * 10.0D) + 30;
      this.func_70536_a((int)(Math.random() * 26.0D + 1.0D + 224.0D));
   }

   public void func_187110_a(double var1, double var3, double var5) {
      this.func_187108_a(this.func_187116_l().func_72317_d(var1, var3, var5));
      this.func_187118_j();
   }

   public int func_189214_a(float var1) {
      int var2 = super.func_189214_a(var1);
      float var3 = (float)this.field_70546_d / (float)this.field_70547_e;
      var3 *= var3;
      var3 *= var3;
      int var4 = var2 & 255;
      int var5 = var2 >> 16 & 255;
      var5 += (int)(var3 * 15.0F * 16.0F);
      if (var5 > 240) {
         var5 = 240;
      }

      return var4 | var5 << 16;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      float var1 = (float)this.field_70546_d / (float)this.field_70547_e;
      var1 = 1.0F - var1;
      float var2 = 1.0F - var1;
      var2 *= var2;
      var2 *= var2;
      this.field_187126_f = this.field_70568_aq + this.field_187129_i * (double)var1;
      this.field_187127_g = this.field_70567_ar + this.field_187130_j * (double)var1 - (double)(var2 * 1.2F);
      this.field_187128_h = this.field_70566_as + this.field_187131_k * (double)var1;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

   }

   public static class NautilusFactory implements IParticleFactory<BasicParticleType> {
      public NautilusFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleEnchantmentTable var15 = new ParticleEnchantmentTable(var2, var3, var5, var7, var9, var11, var13);
         var15.func_70536_a(208);
         return var15;
      }
   }

   public static class EnchantmentTable implements IParticleFactory<BasicParticleType> {
      public EnchantmentTable() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleEnchantmentTable(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
