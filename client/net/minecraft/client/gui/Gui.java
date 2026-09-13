package net.minecraft.client.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

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
      if (var0 < var2) {
         int var5 = var0;
         var0 = var2;
         var2 = var5;
      }

      if (var1 < var3) {
         int var10 = var1;
         var1 = var3;
         var3 = var10;
      }

      float var11 = (float)(var4 >> 24 & 0xFF) / 255.0F;
      float var6 = (float)(var4 >> 16 & 0xFF) / 255.0F;
      float var7 = (float)(var4 >> 8 & 0xFF) / 255.0F;
      float var8 = (float)(var4 & 0xFF) / 255.0F;
      Tessellator var9 = Tessellator.field_78398_a;
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      GL11.glColor4f(var6, var7, var8, var11);
      var9.func_78382_b();
      var9.func_78377_a((double)var0, (double)var3, 0.0);
      var9.func_78377_a((double)var2, (double)var3, 0.0);
      var9.func_78377_a((double)var2, (double)var1, 0.0);
      var9.func_78377_a((double)var0, (double)var1, 0.0);
      var9.func_78381_a();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
   }

   protected void func_73733_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var7 = (float)(var5 >> 24 & 0xFF) / 255.0F;
      float var8 = (float)(var5 >> 16 & 0xFF) / 255.0F;
      float var9 = (float)(var5 >> 8 & 0xFF) / 255.0F;
      float var10 = (float)(var5 & 0xFF) / 255.0F;
      float var11 = (float)(var6 >> 24 & 0xFF) / 255.0F;
      float var12 = (float)(var6 >> 16 & 0xFF) / 255.0F;
      float var13 = (float)(var6 >> 8 & 0xFF) / 255.0F;
      float var14 = (float)(var6 & 0xFF) / 255.0F;
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glDisable(3008);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      GL11.glShadeModel(7425);
      Tessellator var15 = Tessellator.field_78398_a;
      var15.func_78382_b();
      var15.func_78369_a(var8, var9, var10, var7);
      var15.func_78377_a((double)var3, (double)var2, (double)this.field_73735_i);
      var15.func_78377_a((double)var1, (double)var2, (double)this.field_73735_i);
      var15.func_78369_a(var12, var13, var14, var11);
      var15.func_78377_a((double)var1, (double)var4, (double)this.field_73735_i);
      var15.func_78377_a((double)var3, (double)var4, (double)this.field_73735_i);
      var15.func_78381_a();
      GL11.glShadeModel(7424);
      GL11.glDisable(3042);
      GL11.glEnable(3008);
      GL11.glEnable(3553);
   }

   public void func_73732_a(FontRenderer var1, String var2, int var3, int var4, int var5) {
      var1.func_78261_a(var2, var3 - var1.func_78256_a(var2) / 2, var4, var5);
   }

   public void func_73731_b(FontRenderer var1, String var2, int var3, int var4, int var5) {
      var1.func_78261_a(var2, var3, var4, var5);
   }

   public void func_73729_b(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var7 = 0.00390625F;
      float var8 = 0.00390625F;
      Tessellator var9 = Tessellator.field_78398_a;
      var9.func_78382_b();
      var9.func_78374_a(
         (double)(var1 + 0), (double)(var2 + var6), (double)this.field_73735_i, (double)((float)(var3 + 0) * var7), (double)((float)(var4 + var6) * var8)
      );
      var9.func_78374_a(
         (double)(var1 + var5),
         (double)(var2 + var6),
         (double)this.field_73735_i,
         (double)((float)(var3 + var5) * var7),
         (double)((float)(var4 + var6) * var8)
      );
      var9.func_78374_a(
         (double)(var1 + var5), (double)(var2 + 0), (double)this.field_73735_i, (double)((float)(var3 + var5) * var7), (double)((float)(var4 + 0) * var8)
      );
      var9.func_78374_a(
         (double)(var1 + 0), (double)(var2 + 0), (double)this.field_73735_i, (double)((float)(var3 + 0) * var7), (double)((float)(var4 + 0) * var8)
      );
      var9.func_78381_a();
   }

   public void func_94065_a(int var1, int var2, IIcon var3, int var4, int var5) {
      Tessellator var6 = Tessellator.field_78398_a;
      var6.func_78382_b();
      var6.func_78374_a((double)(var1 + 0), (double)(var2 + var5), (double)this.field_73735_i, (double)var3.func_94209_e(), (double)var3.func_94210_h());
      var6.func_78374_a((double)(var1 + var4), (double)(var2 + var5), (double)this.field_73735_i, (double)var3.func_94212_f(), (double)var3.func_94210_h());
      var6.func_78374_a((double)(var1 + var4), (double)(var2 + 0), (double)this.field_73735_i, (double)var3.func_94212_f(), (double)var3.func_94206_g());
      var6.func_78374_a((double)(var1 + 0), (double)(var2 + 0), (double)this.field_73735_i, (double)var3.func_94209_e(), (double)var3.func_94206_g());
      var6.func_78381_a();
   }

   public static void func_146110_a(int var0, int var1, float var2, float var3, int var4, int var5, float var6, float var7) {
      float var8 = 1.0F / var6;
      float var9 = 1.0F / var7;
      Tessellator var10 = Tessellator.field_78398_a;
      var10.func_78382_b();
      var10.func_78374_a((double)var0, (double)(var1 + var5), 0.0, (double)(var2 * var8), (double)((var3 + (float)var5) * var9));
      var10.func_78374_a((double)(var0 + var4), (double)(var1 + var5), 0.0, (double)((var2 + (float)var4) * var8), (double)((var3 + (float)var5) * var9));
      var10.func_78374_a((double)(var0 + var4), (double)var1, 0.0, (double)((var2 + (float)var4) * var8), (double)(var3 * var9));
      var10.func_78374_a((double)var0, (double)var1, 0.0, (double)(var2 * var8), (double)(var3 * var9));
      var10.func_78381_a();
   }

   public static void func_152125_a(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7, float var8, float var9) {
      float var10 = 1.0F / var8;
      float var11 = 1.0F / var9;
      Tessellator var12 = Tessellator.field_78398_a;
      var12.func_78382_b();
      var12.func_78374_a((double)var0, (double)(var1 + var7), 0.0, (double)(var2 * var10), (double)((var3 + (float)var5) * var11));
      var12.func_78374_a((double)(var0 + var6), (double)(var1 + var7), 0.0, (double)((var2 + (float)var4) * var10), (double)((var3 + (float)var5) * var11));
      var12.func_78374_a((double)(var0 + var6), (double)var1, 0.0, (double)((var2 + (float)var4) * var10), (double)(var3 * var11));
      var12.func_78374_a((double)var0, (double)var1, 0.0, (double)(var2 * var10), (double)(var3 * var11));
      var12.func_78381_a();
   }
}
