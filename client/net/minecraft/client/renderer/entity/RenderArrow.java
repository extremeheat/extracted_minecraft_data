package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderArrow extends Render<EntityArrow> {
   private static final ResourceLocation field_110780_a = new ResourceLocation("textures/entity/arrow.png");

   public RenderArrow(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(EntityArrow var1, double var2, double var4, double var6, float var8, float var9) {
      this.func_180548_c(var1);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      GlStateManager.func_179114_b(var1.field_70126_B + (var1.field_70177_z - var1.field_70126_B) * var9 - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9, 0.0F, 0.0F, 1.0F);
      Tessellator var10 = Tessellator.func_178181_a();
      WorldRenderer var11 = var10.func_178180_c();
      byte var12 = 0;
      float var13 = 0.0F;
      float var14 = 0.5F;
      float var15 = (float)(0 + var12 * 10) / 32.0F;
      float var16 = (float)(5 + var12 * 10) / 32.0F;
      float var17 = 0.0F;
      float var18 = 0.15625F;
      float var19 = (float)(5 + var12 * 10) / 32.0F;
      float var20 = (float)(10 + var12 * 10) / 32.0F;
      float var21 = 0.05625F;
      GlStateManager.func_179091_B();
      float var22 = (float)var1.field_70249_b - var9;
      if (var22 > 0.0F) {
         float var23 = -MathHelper.func_76126_a(var22 * 3.0F) * var22;
         GlStateManager.func_179114_b(var23, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_179114_b(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179152_a(var21, var21, var21);
      GlStateManager.func_179109_b(-4.0F, 0.0F, 0.0F);
      GL11.glNormal3f(var21, 0.0F, 0.0F);
      var11.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var11.func_181662_b(-7.0D, -2.0D, -2.0D).func_181673_a((double)var17, (double)var19).func_181675_d();
      var11.func_181662_b(-7.0D, -2.0D, 2.0D).func_181673_a((double)var18, (double)var19).func_181675_d();
      var11.func_181662_b(-7.0D, 2.0D, 2.0D).func_181673_a((double)var18, (double)var20).func_181675_d();
      var11.func_181662_b(-7.0D, 2.0D, -2.0D).func_181673_a((double)var17, (double)var20).func_181675_d();
      var10.func_78381_a();
      GL11.glNormal3f(-var21, 0.0F, 0.0F);
      var11.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var11.func_181662_b(-7.0D, 2.0D, -2.0D).func_181673_a((double)var17, (double)var19).func_181675_d();
      var11.func_181662_b(-7.0D, 2.0D, 2.0D).func_181673_a((double)var18, (double)var19).func_181675_d();
      var11.func_181662_b(-7.0D, -2.0D, 2.0D).func_181673_a((double)var18, (double)var20).func_181675_d();
      var11.func_181662_b(-7.0D, -2.0D, -2.0D).func_181673_a((double)var17, (double)var20).func_181675_d();
      var10.func_78381_a();

      for(int var24 = 0; var24 < 4; ++var24) {
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         GL11.glNormal3f(0.0F, 0.0F, var21);
         var11.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var11.func_181662_b(-8.0D, -2.0D, 0.0D).func_181673_a((double)var13, (double)var15).func_181675_d();
         var11.func_181662_b(8.0D, -2.0D, 0.0D).func_181673_a((double)var14, (double)var15).func_181675_d();
         var11.func_181662_b(8.0D, 2.0D, 0.0D).func_181673_a((double)var14, (double)var16).func_181675_d();
         var11.func_181662_b(-8.0D, 2.0D, 0.0D).func_181673_a((double)var13, (double)var16).func_181675_d();
         var10.func_78381_a();
      }

      GlStateManager.func_179101_C();
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityArrow var1) {
      return field_110780_a;
   }
}
