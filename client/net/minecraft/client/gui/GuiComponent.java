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
import com.mojang.math.Divisor;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public abstract class GuiComponent {
   public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
   public static final ResourceLocation LIGHT_DIRT_BACKGROUND = new ResourceLocation("textures/gui/light_dirt_background.png");
   private static final GuiComponent.ScissorStack SCISSOR_STACK = new GuiComponent.ScissorStack();

   public GuiComponent() {
      super();
   }

   protected static void hLine(PoseStack var0, int var1, int var2, int var3, int var4) {
      if (var2 < var1) {
         int var5 = var1;
         var1 = var2;
         var2 = var5;
      }

      fill(var0, var1, var3, var2 + 1, var3 + 1, var4);
   }

   protected static void vLine(PoseStack var0, int var1, int var2, int var3, int var4) {
      if (var3 < var2) {
         int var5 = var2;
         var2 = var3;
         var3 = var5;
      }

      fill(var0, var1, var2 + 1, var1 + 1, var3, var4);
   }

   public static void enableScissor(int var0, int var1, int var2, int var3) {
      applyScissor(SCISSOR_STACK.push(new ScreenRectangle(var0, var1, var2 - var0, var3 - var1)));
   }

   public static void disableScissor() {
      applyScissor(SCISSOR_STACK.pop());
   }

   private static void applyScissor(@Nullable ScreenRectangle var0) {
      if (var0 != null) {
         Window var1 = Minecraft.getInstance().getWindow();
         int var2 = var1.getHeight();
         double var3 = var1.getGuiScale();
         double var5 = (double)var0.left() * var3;
         double var7 = (double)var2 - (double)var0.bottom() * var3;
         double var9 = (double)var0.width() * var3;
         double var11 = (double)var0.height() * var3;
         RenderSystem.enableScissor((int)var5, (int)var7, Math.max(0, (int)var9), Math.max(0, (int)var11));
      } else {
         RenderSystem.disableScissor();
      }
   }

   public static void fill(PoseStack var0, int var1, int var2, int var3, int var4, int var5) {
      fill(var0, var1, var2, var3, var4, 0, var5);
   }

   public static void fill(PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      Matrix4f var7 = var0.last().pose();
      if (var1 < var3) {
         int var8 = var1;
         var1 = var3;
         var3 = var8;
      }

      if (var2 < var4) {
         int var13 = var2;
         var2 = var4;
         var4 = var13;
      }

      float var14 = (float)FastColor.ARGB32.alpha(var6) / 255.0F;
      float var9 = (float)FastColor.ARGB32.red(var6) / 255.0F;
      float var10 = (float)FastColor.ARGB32.green(var6) / 255.0F;
      float var11 = (float)FastColor.ARGB32.blue(var6) / 255.0F;
      BufferBuilder var12 = Tesselator.getInstance().getBuilder();
      RenderSystem.enableBlend();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      var12.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      var12.vertex(var7, (float)var1, (float)var2, (float)var5).color(var9, var10, var11, var14).endVertex();
      var12.vertex(var7, (float)var1, (float)var4, (float)var5).color(var9, var10, var11, var14).endVertex();
      var12.vertex(var7, (float)var3, (float)var4, (float)var5).color(var9, var10, var11, var14).endVertex();
      var12.vertex(var7, (float)var3, (float)var2, (float)var5).color(var9, var10, var11, var14).endVertex();
      BufferUploader.drawWithShader(var12.end());
      RenderSystem.disableBlend();
   }

   protected static void fillGradient(PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      fillGradient(var0, var1, var2, var3, var4, var5, var6, 0);
   }

   protected static void fillGradient(PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      RenderSystem.enableBlend();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      Tesselator var8 = Tesselator.getInstance();
      BufferBuilder var9 = var8.getBuilder();
      var9.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      fillGradient(var0.last().pose(), var9, var1, var2, var3, var4, var7, var5, var6);
      var8.end();
      RenderSystem.disableBlend();
   }

   protected static void fillGradient(Matrix4f var0, BufferBuilder var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      float var9 = (float)FastColor.ARGB32.alpha(var7) / 255.0F;
      float var10 = (float)FastColor.ARGB32.red(var7) / 255.0F;
      float var11 = (float)FastColor.ARGB32.green(var7) / 255.0F;
      float var12 = (float)FastColor.ARGB32.blue(var7) / 255.0F;
      float var13 = (float)FastColor.ARGB32.alpha(var8) / 255.0F;
      float var14 = (float)FastColor.ARGB32.red(var8) / 255.0F;
      float var15 = (float)FastColor.ARGB32.green(var8) / 255.0F;
      float var16 = (float)FastColor.ARGB32.blue(var8) / 255.0F;
      var1.vertex(var0, (float)var2, (float)var3, (float)var6).color(var10, var11, var12, var9).endVertex();
      var1.vertex(var0, (float)var2, (float)var5, (float)var6).color(var14, var15, var16, var13).endVertex();
      var1.vertex(var0, (float)var4, (float)var5, (float)var6).color(var14, var15, var16, var13).endVertex();
      var1.vertex(var0, (float)var4, (float)var3, (float)var6).color(var10, var11, var12, var9).endVertex();
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

   public static void blitOutlineBlack(int var0, int var1, BiConsumer<Integer, Integer> var2) {
      RenderSystem.blendFuncSeparate(
         GlStateManager.SourceFactor.ZERO,
         GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
         GlStateManager.SourceFactor.SRC_ALPHA,
         GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
      );
      var2.accept(var0 + 1, var1);
      var2.accept(var0 - 1, var1);
      var2.accept(var0, var1 + 1);
      var2.accept(var0, var1 - 1);
      RenderSystem.defaultBlendFunc();
      var2.accept(var0, var1);
   }

   public static void blit(PoseStack var0, int var1, int var2, int var3, int var4, int var5, TextureAtlasSprite var6) {
      innerBlit(var0.last().pose(), var1, var1 + var4, var2, var2 + var5, var3, var6.getU0(), var6.getU1(), var6.getV0(), var6.getV1());
   }

   public static void blit(
      PoseStack var0, int var1, int var2, int var3, int var4, int var5, TextureAtlasSprite var6, float var7, float var8, float var9, float var10
   ) {
      innerBlit(
         var0.last().pose(), var1, var1 + var4, var2, var2 + var5, var3, var6.getU0(), var6.getU1(), var6.getV0(), var6.getV1(), var7, var8, var9, var10
      );
   }

   public static void renderOutline(PoseStack var0, int var1, int var2, int var3, int var4, int var5) {
      fill(var0, var1, var2, var1 + var3, var2 + 1, var5);
      fill(var0, var1, var2 + var4 - 1, var1 + var3, var2 + var4, var5);
      fill(var0, var1, var2 + 1, var1 + 1, var2 + var4 - 1, var5);
      fill(var0, var1 + var3 - 1, var2 + 1, var1 + var3, var2 + var4 - 1, var5);
   }

   public static void blit(PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      blit(var0, var1, var2, 0, (float)var3, (float)var4, var5, var6, 256, 256);
   }

   public static void blit(PoseStack var0, int var1, int var2, int var3, float var4, float var5, int var6, int var7, int var8, int var9) {
      blit(var0, var1, var1 + var6, var2, var2 + var7, var3, var6, var7, var4, var5, var8, var9);
   }

   public static void blit(PoseStack var0, int var1, int var2, int var3, int var4, float var5, float var6, int var7, int var8, int var9, int var10) {
      blit(var0, var1, var1 + var3, var2, var2 + var4, 0, var7, var8, var5, var6, var9, var10);
   }

   public static void blit(PoseStack var0, int var1, int var2, float var3, float var4, int var5, int var6, int var7, int var8) {
      blit(var0, var1, var2, var5, var6, var3, var4, var5, var6, var7, var8);
   }

   private static void blit(PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, float var8, float var9, int var10, int var11) {
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
      var10.vertex(var0, (float)var1, (float)var3, (float)var5).uv(var6, var8).endVertex();
      var10.vertex(var0, (float)var1, (float)var4, (float)var5).uv(var6, var9).endVertex();
      var10.vertex(var0, (float)var2, (float)var4, (float)var5).uv(var7, var9).endVertex();
      var10.vertex(var0, (float)var2, (float)var3, (float)var5).uv(var7, var8).endVertex();
      BufferUploader.drawWithShader(var10.end());
   }

   private static void innerBlit(
      Matrix4f var0,
      int var1,
      int var2,
      int var3,
      int var4,
      int var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13
   ) {
      RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
      RenderSystem.enableBlend();
      BufferBuilder var14 = Tesselator.getInstance().getBuilder();
      var14.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
      var14.vertex(var0, (float)var1, (float)var3, (float)var5).color(var10, var11, var12, var13).uv(var6, var8).endVertex();
      var14.vertex(var0, (float)var1, (float)var4, (float)var5).color(var10, var11, var12, var13).uv(var6, var9).endVertex();
      var14.vertex(var0, (float)var2, (float)var4, (float)var5).color(var10, var11, var12, var13).uv(var7, var9).endVertex();
      var14.vertex(var0, (float)var2, (float)var3, (float)var5).color(var10, var11, var12, var13).uv(var7, var8).endVertex();
      BufferUploader.drawWithShader(var14.end());
      RenderSystem.disableBlend();
   }

   public static void blitNineSliced(PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      blitNineSliced(var0, var1, var2, var3, var4, var5, var5, var5, var5, var6, var7, var8, var9);
   }

   public static void blitNineSliced(PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      blitNineSliced(var0, var1, var2, var3, var4, var5, var6, var5, var6, var7, var8, var9, var10);
   }

   public static void blitNineSliced(
      PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12
   ) {
      var5 = Math.min(var5, var3 / 2);
      var7 = Math.min(var7, var3 / 2);
      var6 = Math.min(var6, var4 / 2);
      var8 = Math.min(var8, var4 / 2);
      if (var3 == var9 && var4 == var10) {
         blit(var0, var1, var2, var11, var12, var3, var4);
      } else if (var4 == var10) {
         blit(var0, var1, var2, var11, var12, var5, var4);
         blitRepeating(var0, var1 + var5, var2, var3 - var7 - var5, var4, var11 + var5, var12, var9 - var7 - var5, var10);
         blit(var0, var1 + var3 - var7, var2, var11 + var9 - var7, var12, var7, var4);
      } else if (var3 == var9) {
         blit(var0, var1, var2, var11, var12, var3, var6);
         blitRepeating(var0, var1, var2 + var6, var3, var4 - var8 - var6, var11, var12 + var6, var9, var10 - var8 - var6);
         blit(var0, var1, var2 + var4 - var8, var11, var12 + var10 - var8, var3, var8);
      } else {
         blit(var0, var1, var2, var11, var12, var5, var6);
         blitRepeating(var0, var1 + var5, var2, var3 - var7 - var5, var6, var11 + var5, var12, var9 - var7 - var5, var6);
         blit(var0, var1 + var3 - var7, var2, var11 + var9 - var7, var12, var7, var6);
         blit(var0, var1, var2 + var4 - var8, var11, var12 + var10 - var8, var5, var8);
         blitRepeating(var0, var1 + var5, var2 + var4 - var8, var3 - var7 - var5, var8, var11 + var5, var12 + var10 - var8, var9 - var7 - var5, var8);
         blit(var0, var1 + var3 - var7, var2 + var4 - var8, var11 + var9 - var7, var12 + var10 - var8, var7, var8);
         blitRepeating(var0, var1, var2 + var6, var5, var4 - var8 - var6, var11, var12 + var6, var5, var10 - var8 - var6);
         blitRepeating(
            var0, var1 + var5, var2 + var6, var3 - var7 - var5, var4 - var8 - var6, var11 + var5, var12 + var6, var9 - var7 - var5, var10 - var8 - var6
         );
         blitRepeating(var0, var1 + var3 - var7, var2 + var6, var5, var4 - var8 - var6, var11 + var9 - var7, var12 + var6, var7, var10 - var8 - var6);
      }
   }

   public static void blitRepeating(PoseStack var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      int var9 = var1;

      int var11;
      for(IntIterator var10 = slices(var3, var7); var10.hasNext(); var9 += var11) {
         var11 = var10.nextInt();
         int var12 = (var7 - var11) / 2;
         int var13 = var2;

         int var15;
         for(IntIterator var14 = slices(var4, var8); var14.hasNext(); var13 += var15) {
            var15 = var14.nextInt();
            int var16 = (var8 - var15) / 2;
            blit(var0, var9, var13, var5 + var12, var6 + var16, var11, var15);
         }
      }
   }

   private static IntIterator slices(int var0, int var1) {
      int var2 = Mth.positiveCeilDiv(var0, var1);
      return new Divisor(var0, var2);
   }

   static class ScissorStack {
      private final Deque<ScreenRectangle> stack = new ArrayDeque<>();

      ScissorStack() {
         super();
      }

      public ScreenRectangle push(ScreenRectangle var1) {
         ScreenRectangle var2 = this.stack.peekLast();
         if (var2 != null) {
            ScreenRectangle var3 = Objects.requireNonNullElse(var1.intersection(var2), ScreenRectangle.empty());
            this.stack.addLast(var3);
            return var3;
         } else {
            this.stack.addLast(var1);
            return var1;
         }
      }

      @Nullable
      public ScreenRectangle pop() {
         if (this.stack.isEmpty()) {
            throw new IllegalStateException("Scissor stack underflow");
         } else {
            this.stack.removeLast();
            return this.stack.peekLast();
         }
      }
   }
}
