package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Gui {
   public static final ResourceLocation field_110325_k = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation field_110323_l = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation field_110324_m = new ResourceLocation("textures/gui/icons.png");
   protected float field_73735_i;

   public Gui() {
      super();
   }

   protected void func_73730_a(int var1, int var2, int var3, int var4) {
      if (var2 < var1) {
         int var5 = var1;
         var1 = var2;
         var2 = var5;
      }

      func_73734_a(var1, var3, var2 + 1, var3 + 1, var4);
   }

   protected void func_73728_b(int var1, int var2, int var3, int var4) {
      if (var3 < var2) {
         int var5 = var2;
         var2 = var3;
         var3 = var5;
      }

      func_73734_a(var1, var2 + 1, var1 + 1, var3, var4);
   }

   public static void func_73734_a(int var0, int var1, int var2, int var3, int var4) {
      int var5;
      if (var0 < var2) {
         var5 = var0;
         var0 = var2;
         var2 = var5;
      }

      if (var1 < var3) {
         var5 = var1;
         var1 = var3;
         var3 = var5;
      }

      float var11 = (float)(var4 >> 24 & 255) / 255.0F;
      float var6 = (float)(var4 >> 16 & 255) / 255.0F;
      float var7 = (float)(var4 >> 8 & 255) / 255.0F;
      float var8 = (float)(var4 & 255) / 255.0F;
      Tessellator var9 = Tessellator.func_178181_a();
      WorldRenderer var10 = var9.func_178180_c();
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179131_c(var6, var7, var8, var11);
      var10.func_181668_a(7, DefaultVertexFormats.field_181705_e);
      var10.func_181662_b((double)var0, (double)var3, 0.0D).func_181675_d();
      var10.func_181662_b((double)var2, (double)var3, 0.0D).func_181675_d();
      var10.func_181662_b((double)var2, (double)var1, 0.0D).func_181675_d();
      var10.func_181662_b((double)var0, (double)var1, 0.0D).func_181675_d();
      var9.func_78381_a();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   protected void func_73733_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var7 = (float)(var5 >> 24 & 255) / 255.0F;
      float var8 = (float)(var5 >> 16 & 255) / 255.0F;
      float var9 = (float)(var5 >> 8 & 255) / 255.0F;
      float var10 = (float)(var5 & 255) / 255.0F;
      float var11 = (float)(var6 >> 24 & 255) / 255.0F;
      float var12 = (float)(var6 >> 16 & 255) / 255.0F;
      float var13 = (float)(var6 >> 8 & 255) / 255.0F;
      float var14 = (float)(var6 & 255) / 255.0F;
      GlStateManager.func_179090_x();
      GlStateManager.func_179147_l();
      GlStateManager.func_179118_c();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179103_j(7425);
      Tessellator var15 = Tessellator.func_178181_a();
      WorldRenderer var16 = var15.func_178180_c();
      var16.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      var16.func_181662_b((double)var3, (double)var2, (double)this.field_73735_i).func_181666_a(var8, var9, var10, var7).func_181675_d();
      var16.func_181662_b((double)var1, (double)var2, (double)this.field_73735_i).func_181666_a(var8, var9, var10, var7).func_181675_d();
      var16.func_181662_b((double)var1, (double)var4, (double)this.field_73735_i).func_181666_a(var12, var13, var14, var11).func_181675_d();
      var16.func_181662_b((double)var3, (double)var4, (double)this.field_73735_i).func_181666_a(var12, var13, var14, var11).func_181675_d();
      var15.func_78381_a();
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179084_k();
      GlStateManager.func_179141_d();
      GlStateManager.func_179098_w();
   }

   public void func_73732_a(FontRenderer var1, String var2, int var3, int var4, int var5) {
      var1.func_175063_a(var2, (float)(var3 - var1.func_78256_a(var2) / 2), (float)var4, var5);
   }

   public void func_73731_b(FontRenderer var1, String var2, int var3, int var4, int var5) {
      var1.func_175063_a(var2, (float)var3, (float)var4, var5);
   }

   public void func_73729_b(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var7 = 0.00390625F;
      float var8 = 0.00390625F;
      Tessellator var9 = Tessellator.func_178181_a();
      WorldRenderer var10 = var9.func_178180_c();
      var10.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var10.func_181662_b((double)(var1 + 0), (double)(var2 + var6), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + 0) * var7), (double)((float)(var4 + var6) * var8)).func_181675_d();
      var10.func_181662_b((double)(var1 + var5), (double)(var2 + var6), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + var5) * var7), (double)((float)(var4 + var6) * var8)).func_181675_d();
      var10.func_181662_b((double)(var1 + var5), (double)(var2 + 0), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + var5) * var7), (double)((float)(var4 + 0) * var8)).func_181675_d();
      var10.func_181662_b((double)(var1 + 0), (double)(var2 + 0), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + 0) * var7), (double)((float)(var4 + 0) * var8)).func_181675_d();
      var9.func_78381_a();
   }

   public void func_175174_a(float var1, float var2, int var3, int var4, int var5, int var6) {
      float var7 = 0.00390625F;
      float var8 = 0.00390625F;
      Tessellator var9 = Tessellator.func_178181_a();
      WorldRenderer var10 = var9.func_178180_c();
      var10.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var10.func_181662_b((double)(var1 + 0.0F), (double)(var2 + (float)var6), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + 0) * var7), (double)((float)(var4 + var6) * var8)).func_181675_d();
      var10.func_181662_b((double)(var1 + (float)var5), (double)(var2 + (float)var6), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + var5) * var7), (double)((float)(var4 + var6) * var8)).func_181675_d();
      var10.func_181662_b((double)(var1 + (float)var5), (double)(var2 + 0.0F), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + var5) * var7), (double)((float)(var4 + 0) * var8)).func_181675_d();
      var10.func_181662_b((double)(var1 + 0.0F), (double)(var2 + 0.0F), (double)this.field_73735_i).func_181673_a((double)((float)(var3 + 0) * var7), (double)((float)(var4 + 0) * var8)).func_181675_d();
      var9.func_78381_a();
   }

   public void func_175175_a(int var1, int var2, TextureAtlasSprite var3, int var4, int var5) {
      Tessellator var6 = Tessellator.func_178181_a();
      WorldRenderer var7 = var6.func_178180_c();
      var7.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var7.func_181662_b((double)(var1 + 0), (double)(var2 + var5), (double)this.field_73735_i).func_181673_a((double)var3.func_94209_e(), (double)var3.func_94210_h()).func_181675_d();
      var7.func_181662_b((double)(var1 + var4), (double)(var2 + var5), (double)this.field_73735_i).func_181673_a((double)var3.func_94212_f(), (double)var3.func_94210_h()).func_181675_d();
      var7.func_181662_b((double)(var1 + var4), (double)(var2 + 0), (double)this.field_73735_i).func_181673_a((double)var3.func_94212_f(), (double)var3.func_94206_g()).func_181675_d();
      var7.func_181662_b((double)(var1 + 0), (double)(var2 + 0), (double)this.field_73735_i).func_181673_a((double)var3.func_94209_e(), (double)var3.func_94206_g()).func_181675_d();
      var6.func_78381_a();
   }

   public static void func_146110_a(int var0, int var1, float var2, float var3, int var4, int var5, float var6, float var7) {
      float var8 = 1.0F / var6;
      float var9 = 1.0F / var7;
      Tessellator var10 = Tessellator.func_178181_a();
      WorldRenderer var11 = var10.func_178180_c();
      var11.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var11.func_181662_b((double)var0, (double)(var1 + var5), 0.0D).func_181673_a((double)(var2 * var8), (double)((var3 + (float)var5) * var9)).func_181675_d();
      var11.func_181662_b((double)(var0 + var4), (double)(var1 + var5), 0.0D).func_181673_a((double)((var2 + (float)var4) * var8), (double)((var3 + (float)var5) * var9)).func_181675_d();
      var11.func_181662_b((double)(var0 + var4), (double)var1, 0.0D).func_181673_a((double)((var2 + (float)var4) * var8), (double)(var3 * var9)).func_181675_d();
      var11.func_181662_b((double)var0, (double)var1, 0.0D).func_181673_a((double)(var2 * var8), (double)(var3 * var9)).func_181675_d();
      var10.func_78381_a();
   }

   public static void func_152125_a(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7, float var8, float var9) {
      float var10 = 1.0F / var8;
      float var11 = 1.0F / var9;
      Tessellator var12 = Tessellator.func_178181_a();
      WorldRenderer var13 = var12.func_178180_c();
      var13.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var13.func_181662_b((double)var0, (double)(var1 + var7), 0.0D).func_181673_a((double)(var2 * var10), (double)((var3 + (float)var5) * var11)).func_181675_d();
      var13.func_181662_b((double)(var0 + var6), (double)(var1 + var7), 0.0D).func_181673_a((double)((var2 + (float)var4) * var10), (double)((var3 + (float)var5) * var11)).func_181675_d();
      var13.func_181662_b((double)(var0 + var6), (double)var1, 0.0D).func_181673_a((double)((var2 + (float)var4) * var10), (double)(var3 * var11)).func_181675_d();
      var13.func_181662_b((double)var0, (double)var1, 0.0D).func_181673_a((double)(var2 * var10), (double)(var3 * var11)).func_181675_d();
      var12.func_78381_a();
   }
}
