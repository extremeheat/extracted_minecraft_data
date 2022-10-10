package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.ModelTrident;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.util.ResourceLocation;

public class RenderTrident extends Render<EntityTrident> {
   public static final ResourceLocation field_203087_a = new ResourceLocation("textures/entity/trident.png");
   private final ModelTrident field_203088_f = new ModelTrident();

   public RenderTrident(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(EntityTrident var1, double var2, double var4, double var6, float var8, float var9) {
      this.func_180548_c(var1);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179094_E();
      GlStateManager.func_179140_f();
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      GlStateManager.func_179114_b(var1.field_70126_B + (var1.field_70177_z - var1.field_70126_B) * var9 - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9 + 90.0F, 0.0F, 0.0F, 1.0F);
      this.field_203088_f.func_203079_a();
      GlStateManager.func_179121_F();
      this.func_203085_b(var1, var2, var4, var6, var8, var9);
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
      GlStateManager.func_179145_e();
   }

   protected ResourceLocation func_110775_a(EntityTrident var1) {
      return field_203087_a;
   }

   private double func_203086_a(double var1, double var3, double var5) {
      return var1 + (var3 - var1) * var5;
   }

   protected void func_203085_b(EntityTrident var1, double var2, double var4, double var6, float var8, float var9) {
      Entity var10 = var1.func_212360_k();
      if (var10 != null && var1.func_203047_q()) {
         Tessellator var11 = Tessellator.func_178181_a();
         BufferBuilder var12 = var11.func_178180_c();
         double var13 = this.func_203086_a((double)var10.field_70126_B, (double)var10.field_70177_z, (double)(var9 * 0.5F)) * 0.01745329238474369D;
         double var15 = Math.cos(var13);
         double var17 = Math.sin(var13);
         double var19 = this.func_203086_a(var10.field_70169_q, var10.field_70165_t, (double)var9);
         double var21 = this.func_203086_a(var10.field_70167_r + (double)var10.func_70047_e() * 0.8D, var10.field_70163_u + (double)var10.func_70047_e() * 0.8D, (double)var9);
         double var23 = this.func_203086_a(var10.field_70166_s, var10.field_70161_v, (double)var9);
         double var25 = var15 - var17;
         double var27 = var17 + var15;
         double var29 = this.func_203086_a(var1.field_70169_q, var1.field_70165_t, (double)var9);
         double var31 = this.func_203086_a(var1.field_70167_r, var1.field_70163_u, (double)var9);
         double var33 = this.func_203086_a(var1.field_70166_s, var1.field_70161_v, (double)var9);
         double var35 = (double)((float)(var19 - var29));
         double var37 = (double)((float)(var21 - var31));
         double var39 = (double)((float)(var23 - var33));
         double var41 = Math.sqrt(var35 * var35 + var37 * var37 + var39 * var39);
         int var43 = var1.func_145782_y() + var1.field_70173_aa;
         double var44 = (double)((float)var43 + var9) * -0.1D;
         double var46 = Math.min(0.5D, var41 / 30.0D);
         GlStateManager.func_179090_x();
         GlStateManager.func_179140_f();
         GlStateManager.func_179129_p();
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 255.0F, 255.0F);
         var12.func_181668_a(5, DefaultVertexFormats.field_181706_f);
         boolean var48 = true;
         int var49 = 7 - var43 % 7;
         double var50 = 0.1D;

         float var64;
         float var65;
         float var66;
         int var52;
         double var53;
         float var55;
         double var56;
         double var58;
         double var60;
         double var62;
         for(var52 = 0; var52 <= 37; ++var52) {
            var53 = (double)var52 / 37.0D;
            var55 = 1.0F - (float)((var52 + var49) % 7) / 7.0F;
            var56 = var53 * 2.0D - 1.0D;
            var56 = (1.0D - var56 * var56) * var46;
            var58 = var2 + var35 * var53 + Math.sin(var53 * 3.141592653589793D * 8.0D + var44) * var25 * var56;
            var60 = var4 + var37 * var53 + Math.cos(var53 * 3.141592653589793D * 8.0D + var44) * 0.02D + (0.1D + var56) * 1.0D;
            var62 = var6 + var39 * var53 + Math.sin(var53 * 3.141592653589793D * 8.0D + var44) * var27 * var56;
            var64 = 0.87F * var55 + 0.3F * (1.0F - var55);
            var65 = 0.91F * var55 + 0.6F * (1.0F - var55);
            var66 = 0.85F * var55 + 0.5F * (1.0F - var55);
            var12.func_181662_b(var58, var60, var62).func_181666_a(var64, var65, var66, 1.0F).func_181675_d();
            var12.func_181662_b(var58 + 0.1D * var56, var60 + 0.1D * var56, var62).func_181666_a(var64, var65, var66, 1.0F).func_181675_d();
            if (var52 > var1.field_203052_f * 2) {
               break;
            }
         }

         var11.func_78381_a();
         var12.func_181668_a(5, DefaultVertexFormats.field_181706_f);

         for(var52 = 0; var52 <= 37; ++var52) {
            var53 = (double)var52 / 37.0D;
            var55 = 1.0F - (float)((var52 + var49) % 7) / 7.0F;
            var56 = var53 * 2.0D - 1.0D;
            var56 = (1.0D - var56 * var56) * var46;
            var58 = var2 + var35 * var53 + Math.sin(var53 * 3.141592653589793D * 8.0D + var44) * var25 * var56;
            var60 = var4 + var37 * var53 + Math.cos(var53 * 3.141592653589793D * 8.0D + var44) * 0.01D + (0.1D + var56) * 1.0D;
            var62 = var6 + var39 * var53 + Math.sin(var53 * 3.141592653589793D * 8.0D + var44) * var27 * var56;
            var64 = 0.87F * var55 + 0.3F * (1.0F - var55);
            var65 = 0.91F * var55 + 0.6F * (1.0F - var55);
            var66 = 0.85F * var55 + 0.5F * (1.0F - var55);
            var12.func_181662_b(var58, var60, var62).func_181666_a(var64, var65, var66, 1.0F).func_181675_d();
            var12.func_181662_b(var58 + 0.1D * var56, var60, var62 + 0.1D * var56).func_181666_a(var64, var65, var66, 1.0F).func_181675_d();
            if (var52 > var1.field_203052_f * 2) {
               break;
            }
         }

         var11.func_78381_a();
         GlStateManager.func_179145_e();
         GlStateManager.func_179098_w();
         GlStateManager.func_179089_o();
      }
   }
}
