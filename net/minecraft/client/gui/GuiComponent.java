package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public abstract class GuiComponent {
   public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
   private int blitOffset;

   protected void hLine(int var1, int var2, int var3, int var4) {
      if (var2 < var1) {
         int var5 = var1;
         var1 = var2;
         var2 = var5;
      }

      fill(var1, var3, var2 + 1, var3 + 1, var4);
   }

   protected void vLine(int var1, int var2, int var3, int var4) {
      if (var3 < var2) {
         int var5 = var2;
         var2 = var3;
         var3 = var5;
      }

      fill(var1, var2 + 1, var1 + 1, var3, var4);
   }

   public static void fill(int var0, int var1, int var2, int var3, int var4) {
      fill(Transformation.identity().getMatrix(), var0, var1, var2, var3, var4);
   }

   public static void fill(Matrix4f var0, int var1, int var2, int var3, int var4, int var5) {
      int var6;
      if (var1 < var3) {
         var6 = var1;
         var1 = var3;
         var3 = var6;
      }

      if (var2 < var4) {
         var6 = var2;
         var2 = var4;
         var4 = var6;
      }

      float var11 = (float)(var5 >> 24 & 255) / 255.0F;
      float var7 = (float)(var5 >> 16 & 255) / 255.0F;
      float var8 = (float)(var5 >> 8 & 255) / 255.0F;
      float var9 = (float)(var5 & 255) / 255.0F;
      BufferBuilder var10 = Tesselator.getInstance().getBuilder();
      RenderSystem.enableBlend();
      RenderSystem.disableTexture();
      RenderSystem.defaultBlendFunc();
      var10.begin(7, DefaultVertexFormat.POSITION_COLOR);
      var10.vertex(var0, (float)var1, (float)var4, 0.0F).color(var7, var8, var9, var11).endVertex();
      var10.vertex(var0, (float)var3, (float)var4, 0.0F).color(var7, var8, var9, var11).endVertex();
      var10.vertex(var0, (float)var3, (float)var2, 0.0F).color(var7, var8, var9, var11).endVertex();
      var10.vertex(var0, (float)var1, (float)var2, 0.0F).color(var7, var8, var9, var11).endVertex();
      var10.end();
      BufferUploader.end(var10);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   protected void fillGradient(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var7 = (float)(var5 >> 24 & 255) / 255.0F;
      float var8 = (float)(var5 >> 16 & 255) / 255.0F;
      float var9 = (float)(var5 >> 8 & 255) / 255.0F;
      float var10 = (float)(var5 & 255) / 255.0F;
      float var11 = (float)(var6 >> 24 & 255) / 255.0F;
      float var12 = (float)(var6 >> 16 & 255) / 255.0F;
      float var13 = (float)(var6 >> 8 & 255) / 255.0F;
      float var14 = (float)(var6 & 255) / 255.0F;
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      RenderSystem.disableAlphaTest();
      RenderSystem.defaultBlendFunc();
      RenderSystem.shadeModel(7425);
      Tesselator var15 = Tesselator.getInstance();
      BufferBuilder var16 = var15.getBuilder();
      var16.begin(7, DefaultVertexFormat.POSITION_COLOR);
      var16.vertex((double)var3, (double)var2, (double)this.blitOffset).color(var8, var9, var10, var7).endVertex();
      var16.vertex((double)var1, (double)var2, (double)this.blitOffset).color(var8, var9, var10, var7).endVertex();
      var16.vertex((double)var1, (double)var4, (double)this.blitOffset).color(var12, var13, var14, var11).endVertex();
      var16.vertex((double)var3, (double)var4, (double)this.blitOffset).color(var12, var13, var14, var11).endVertex();
      var15.end();
      RenderSystem.shadeModel(7424);
      RenderSystem.disableBlend();
      RenderSystem.enableAlphaTest();
      RenderSystem.enableTexture();
   }

   public void drawCenteredString(Font var1, String var2, int var3, int var4, int var5) {
      var1.drawShadow(var2, (float)(var3 - var1.width(var2) / 2), (float)var4, var5);
   }

   public void drawRightAlignedString(Font var1, String var2, int var3, int var4, int var5) {
      var1.drawShadow(var2, (float)(var3 - var1.width(var2)), (float)var4, var5);
   }

   public void drawString(Font var1, String var2, int var3, int var4, int var5) {
      var1.drawShadow(var2, (float)var3, (float)var4, var5);
   }

   public static void blit(int var0, int var1, int var2, int var3, int var4, TextureAtlasSprite var5) {
      innerBlit(var0, var0 + var3, var1, var1 + var4, var2, var5.getU0(), var5.getU1(), var5.getV0(), var5.getV1());
   }

   public void blit(int var1, int var2, int var3, int var4, int var5, int var6) {
      blit(var1, var2, this.blitOffset, (float)var3, (float)var4, var5, var6, 256, 256);
   }

   public static void blit(int var0, int var1, int var2, float var3, float var4, int var5, int var6, int var7, int var8) {
      innerBlit(var0, var0 + var5, var1, var1 + var6, var2, var5, var6, var3, var4, var8, var7);
   }

   public static void blit(int var0, int var1, int var2, int var3, float var4, float var5, int var6, int var7, int var8, int var9) {
      innerBlit(var0, var0 + var2, var1, var1 + var3, 0, var6, var7, var4, var5, var8, var9);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7) {
      blit(var0, var1, var4, var5, var2, var3, var4, var5, var6, var7);
   }

   private static void innerBlit(int var0, int var1, int var2, int var3, int var4, int var5, int var6, float var7, float var8, int var9, int var10) {
      innerBlit(var0, var1, var2, var3, var4, (var7 + 0.0F) / (float)var9, (var7 + (float)var5) / (float)var9, (var8 + 0.0F) / (float)var10, (var8 + (float)var6) / (float)var10);
   }

   protected static void innerBlit(int var0, int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      BufferBuilder var9 = Tesselator.getInstance().getBuilder();
      var9.begin(7, DefaultVertexFormat.POSITION_TEX);
      var9.vertex((double)var0, (double)var3, (double)var4).uv(var5, var8).endVertex();
      var9.vertex((double)var1, (double)var3, (double)var4).uv(var6, var8).endVertex();
      var9.vertex((double)var1, (double)var2, (double)var4).uv(var6, var7).endVertex();
      var9.vertex((double)var0, (double)var2, (double)var4).uv(var5, var7).endVertex();
      var9.end();
      RenderSystem.enableAlphaTest();
      BufferUploader.end(var9);
   }

   public int getBlitOffset() {
      return this.blitOffset;
   }

   public void setBlitOffset(int var1) {
      this.blitOffset = var1;
   }
}
