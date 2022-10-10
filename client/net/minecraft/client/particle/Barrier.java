package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.World;

public class Barrier extends Particle {
   protected Barrier(World var1, double var2, double var4, double var6, IItemProvider var8) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.func_187117_a(Minecraft.func_71410_x().func_175599_af().func_175037_a().func_199934_a(var8));
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.field_187129_i = 0.0D;
      this.field_187130_j = 0.0D;
      this.field_187131_k = 0.0D;
      this.field_70545_g = 0.0F;
      this.field_70547_e = 80;
      this.field_190017_n = false;
   }

   public int func_70537_b() {
      return 1;
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = this.field_187119_C.func_94209_e();
      float var10 = this.field_187119_C.func_94212_f();
      float var11 = this.field_187119_C.func_94206_g();
      float var12 = this.field_187119_C.func_94210_h();
      float var13 = 0.5F;
      float var14 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)var3 - field_70556_an);
      float var15 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)var3 - field_70554_ao);
      float var16 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)var3 - field_70555_ap);
      int var17 = this.func_189214_a(var3);
      int var18 = var17 >> 16 & '\uffff';
      int var19 = var17 & '\uffff';
      var1.func_181662_b((double)(var14 - var4 * 0.5F - var7 * 0.5F), (double)(var15 - var5 * 0.5F), (double)(var16 - var6 * 0.5F - var8 * 0.5F)).func_187315_a((double)var10, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 - var4 * 0.5F + var7 * 0.5F), (double)(var15 + var5 * 0.5F), (double)(var16 - var6 * 0.5F + var8 * 0.5F)).func_187315_a((double)var10, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 + var4 * 0.5F + var7 * 0.5F), (double)(var15 + var5 * 0.5F), (double)(var16 + var6 * 0.5F + var8 * 0.5F)).func_187315_a((double)var9, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 + var4 * 0.5F - var7 * 0.5F), (double)(var15 - var5 * 0.5F), (double)(var16 + var6 * 0.5F - var8 * 0.5F)).func_187315_a((double)var9, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_187314_a(var18, var19).func_181675_d();
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new Barrier(var2, var3, var5, var7, Blocks.field_180401_cv.func_199767_j());
      }
   }
}
