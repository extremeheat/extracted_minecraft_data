package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public abstract class GuiComponent {
   public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
   private int blitOffset;

   public GuiComponent() {
      super();
   }

   protected void hLine(PoseStack var1, int var2, int var3, int var4, int var5) {
      if (var3 < var2) {
         int var6 = var2;
         var2 = var3;
         var3 = var6;
      }

      fill(var1, var2, var4, var3 + 1, var4 + 1, var5);
   }

   protected void vLine(PoseStack var1, int var2, int var3, int var4, int var5) {
      if (var4 < var3) {
         int var6 = var3;
         var3 = var4;
         var4 = var6;
      }

      fill(var1, var2, var3 + 1, var2 + 1, var4, var5);
   }

   public static void enableScissor(int var0, int var1, int var2, int var3) {
      Window var4 = Minecraft.getInstance().getWindow();
      int var5 = var4.getHeight();
      double var6 = var4.getGuiScale();
      double var8 = (double)var0 * var6;
      double var10 = (double)var5 - (double)var3 * var6;
      double var12 = (double)(var2 - var0) * var6;
      double var14 = (double)(var3 - var1) * var6;
      RenderSystem.enableScissor((int)var8, (int)var10, Math.max(0, (int)var12), Math.max(0, (int)var14));
   }

   public static void disableScissor() {
      RenderSystem.disableScissor();
   }

   public static void fill(PoseStack var0, int var1, int var2, int var3, int var4, int var5) {
      innerFill(var0.last().pose(), var1, var2, var3, var4, var5);
   }

   private static void innerFill(Matrix4f var0, int var1, int var2, int var3, int var4, int var5) {
      if (var1 < var3) {
         int var6 = var1;
         var1 = var3;
         var3 = var6;
      }

      if (var2 < var4) {
         int var11 = var2;
         var2 = var4;
         var4 = var11;
      }

      float var12 = (float)(var5 >> 24 & 0xFF) / 255.0F;
      float var7 = (float)(var5 >> 16 & 0xFF) / 255.0F;
      float var8 = (float)(var5 >> 8 & 0xFF) / 255.0F;
      float var9 = (float)(var5 & 0xFF) / 255.0F;
      BufferBuilder var10 = Tesselator.getInstance().getBuilder();
      RenderSystem.enableBlend();
      RenderSystem.disableTexture();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      var10.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      var10.vertex(var0, (float)var1, (float)var4, 0.0F).color(var7, var8, var9, var12).endVertex();
      var10.vertex(var0, (float)var3, (float)var4, 0.0F).color(var7, var8, var9, var12).endVertex();
      var10.vertex(var0, (float)var3, (float)var2, 0.0F).color(var7, var8, var9, var12).endVertex();
      var10.vertex(var0, (float)var1, (float)var2, 0.0F).color(var7, var8, var9, var12).endVertex();
      BufferUploader.drawWithShader(var10.end());
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   protected void fillGradient(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      fillGradient(var1, var2, var3, var4, var5, var6, var7, this.blitOffset);
   }

   protected static void fillGradient(PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      Tesselator var8 = Tesselator.getInstance();
      BufferBuilder var9 = var8.getBuilder();
      var9.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      fillGradient(var0.last().pose(), var9, var1, var2, var3, var4, var7, var5, var6);
      var8.end();
      RenderSystem.disableBlend();
      RenderSystem.enableTexture();
   }

   protected static void fillGradient(Matrix4f var0, BufferBuilder var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      float var9 = (float)(var7 >> 24 & 0xFF) / 255.0F;
      float var10 = (float)(var7 >> 16 & 0xFF) / 255.0F;
      float var11 = (float)(var7 >> 8 & 0xFF) / 255.0F;
      float var12 = (float)(var7 & 0xFF) / 255.0F;
      float var13 = (float)(var8 >> 24 & 0xFF) / 255.0F;
      float var14 = (float)(var8 >> 16 & 0xFF) / 255.0F;
      float var15 = (float)(var8 >> 8 & 0xFF) / 255.0F;
      float var16 = (float)(var8 & 0xFF) / 255.0F;
      var1.vertex(var0, (float)var4, (float)var3, (float)var6).color(var10, var11, var12, var9).endVertex();
      var1.vertex(var0, (float)var2, (float)var3, (float)var6).color(var10, var11, var12, var9).endVertex();
      var1.vertex(var0, (float)var2, (float)var5, (float)var6).color(var14, var15, var16, var13).endVertex();
      var1.vertex(var0, (float)var4, (float)var5, (float)var6).color(var14, var15, var16, var13).endVertex();
   }

   public static void drawCenteredString(PoseStack var0, Font var1, String var2, int var3, int var4, int var5) {
      var1.drawShadow(var0, var2, (float)(var3 - var1.width(var2) / 2), (float)var4, var5);
   }

   public static void drawCenteredString(PoseStack var0, Font var1, Component var2, int var3, int var4, int var5) {
      FormattedCharSequence var6 = var2.getVisualOrderText();
      var1.drawShadow(var0, var6, (float)(var3 - var1.width(var6) / 2), (float)var4, var5);
   }

   public static void drawCenteredString(PoseStack var0, Font var1, FormattedCharSequence var2, int var3, int var4, int var5) {
      var1.drawShadow(var0, var2, (float)(var3 - var1.width(var2) / 2), (float)var4, var5);
   }

   public static void drawString(PoseStack var0, Font var1, String var2, int var3, int var4, int var5) {
      var1.drawShadow(var0, var2, (float)var3, (float)var4, var5);
   }

   public static void drawString(PoseStack var0, Font var1, FormattedCharSequence var2, int var3, int var4, int var5) {
      var1.drawShadow(var0, var2, (float)var3, (float)var4, var5);
   }

   public static void drawString(PoseStack var0, Font var1, Component var2, int var3, int var4, int var5) {
      var1.drawShadow(var0, var2, (float)var3, (float)var4, var5);
   }

   public void blitOutlineBlack(int var1, int var2, BiConsumer<Integer, Integer> var3) {
      RenderSystem.blendFuncSeparate(
         GlStateManager.SourceFactor.ZERO,
         GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
         GlStateManager.SourceFactor.SRC_ALPHA,
         GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
      );
      var3.accept(var1 + 1, var2);
      var3.accept(var1 - 1, var2);
      var3.accept(var1, var2 + 1);
      var3.accept(var1, var2 - 1);
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      var3.accept(var1, var2);
   }

   public static void blit(PoseStack var0, int var1, int var2, int var3, int var4, int var5, TextureAtlasSprite var6) {
      innerBlit(var0.last().pose(), var1, var1 + var4, var2, var2 + var5, var3, var6.getU0(), var6.getU1(), var6.getV0(), var6.getV1());
   }

   public void blit(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      blit(var1, var2, var3, this.blitOffset, (float)var4, (float)var5, var6, var7, 256, 256);
   }

   public static void blit(PoseStack var0, int var1, int var2, int var3, float var4, float var5, int var6, int var7, int var8, int var9) {
      innerBlit(var0, var1, var1 + var6, var2, var2 + var7, var3, var6, var7, var4, var5, var8, var9);
   }

   public static void blit(PoseStack var0, int var1, int var2, int var3, int var4, float var5, float var6, int var7, int var8, int var9, int var10) {
      innerBlit(var0, var1, var1 + var3, var2, var2 + var4, 0, var7, var8, var5, var6, var9, var10);
   }

   public static void blit(PoseStack var0, int var1, int var2, float var3, float var4, int var5, int var6, int var7, int var8) {
      blit(var0, var1, var2, var5, var6, var3, var4, var5, var6, var7, var8);
   }

   private static void innerBlit(
      PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, float var8, float var9, int var10, int var11
   ) {
      innerBlit(
         var0.last().pose(),
         var1,
         var2,
         var3,
         var4,
         var5,
         (var8 + 0.0F) / (float)var10,
         (var8 + (float)var6) / (float)var10,
         (var9 + 0.0F) / (float)var11,
         (var9 + (float)var7) / (float)var11
      );
   }

   private static void innerBlit(Matrix4f var0, int var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      BufferBuilder var10 = Tesselator.getInstance().getBuilder();
      var10.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      var10.vertex(var0, (float)var1, (float)var4, (float)var5).uv(var6, var9).endVertex();
      var10.vertex(var0, (float)var2, (float)var4, (float)var5).uv(var7, var9).endVertex();
      var10.vertex(var0, (float)var2, (float)var3, (float)var5).uv(var7, var8).endVertex();
      var10.vertex(var0, (float)var1, (float)var3, (float)var5).uv(var6, var8).endVertex();
      BufferUploader.drawWithShader(var10.end());
   }

   public int getBlitOffset() {
      return this.blitOffset;
   }

   public void setBlitOffset(int var1) {
      this.blitOffset = var1;
   }
}
