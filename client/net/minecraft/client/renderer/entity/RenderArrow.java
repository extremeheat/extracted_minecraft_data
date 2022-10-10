package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.MathHelper;

public abstract class RenderArrow<T extends EntityArrow> extends Render<T> {
   public RenderArrow(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(T var1, double var2, double var4, double var6, float var8, float var9) {
      this.func_180548_c(var1);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179094_E();
      GlStateManager.func_179140_f();
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      GlStateManager.func_179114_b(var1.field_70126_B + (var1.field_70177_z - var1.field_70126_B) * var9 - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9, 0.0F, 0.0F, 1.0F);
      Tessellator var10 = Tessellator.func_178181_a();
      BufferBuilder var11 = var10.func_178180_c();
      boolean var12 = false;
      float var13 = 0.0F;
      float var14 = 0.5F;
      float var15 = 0.0F;
      float var16 = 0.15625F;
      float var17 = 0.0F;
      float var18 = 0.15625F;
      float var19 = 0.15625F;
      float var20 = 0.3125F;
      float var21 = 0.05625F;
      GlStateManager.func_179091_B();
      float var22 = (float)var1.field_70249_b - var9;
      if (var22 > 0.0F) {
         float var23 = -MathHelper.func_76126_a(var22 * 3.0F) * var22;
         GlStateManager.func_179114_b(var23, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_179114_b(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179152_a(0.05625F, 0.05625F, 0.05625F);
      GlStateManager.func_179109_b(-4.0F, 0.0F, 0.0F);
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      GlStateManager.func_187432_a(0.05625F, 0.0F, 0.0F);
      var11.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var11.func_181662_b(-7.0D, -2.0D, -2.0D).func_187315_a(0.0D, 0.15625D).func_181675_d();
      var11.func_181662_b(-7.0D, -2.0D, 2.0D).func_187315_a(0.15625D, 0.15625D).func_181675_d();
      var11.func_181662_b(-7.0D, 2.0D, 2.0D).func_187315_a(0.15625D, 0.3125D).func_181675_d();
      var11.func_181662_b(-7.0D, 2.0D, -2.0D).func_187315_a(0.0D, 0.3125D).func_181675_d();
      var10.func_78381_a();
      GlStateManager.func_187432_a(-0.05625F, 0.0F, 0.0F);
      var11.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var11.func_181662_b(-7.0D, 2.0D, -2.0D).func_187315_a(0.0D, 0.15625D).func_181675_d();
      var11.func_181662_b(-7.0D, 2.0D, 2.0D).func_187315_a(0.15625D, 0.15625D).func_181675_d();
      var11.func_181662_b(-7.0D, -2.0D, 2.0D).func_187315_a(0.15625D, 0.3125D).func_181675_d();
      var11.func_181662_b(-7.0D, -2.0D, -2.0D).func_187315_a(0.0D, 0.3125D).func_181675_d();
      var10.func_78381_a();

      for(int var24 = 0; var24 < 4; ++var24) {
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_187432_a(0.0F, 0.0F, 0.05625F);
         var11.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var11.func_181662_b(-8.0D, -2.0D, 0.0D).func_187315_a(0.0D, 0.0D).func_181675_d();
         var11.func_181662_b(8.0D, -2.0D, 0.0D).func_187315_a(0.5D, 0.0D).func_181675_d();
         var11.func_181662_b(8.0D, 2.0D, 0.0D).func_187315_a(0.5D, 0.15625D).func_181675_d();
         var11.func_181662_b(-8.0D, 2.0D, 0.0D).func_187315_a(0.0D, 0.15625D).func_181675_d();
         var10.func_78381_a();
      }

      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179101_C();
      GlStateManager.func_179145_e();
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }
}
