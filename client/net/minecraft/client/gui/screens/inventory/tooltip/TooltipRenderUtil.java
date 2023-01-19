package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.joml.Matrix4f;

public class TooltipRenderUtil {
   public static final int MOUSE_OFFSET = 12;
   private static final int PADDING = 3;
   public static final int PADDING_LEFT = 3;
   public static final int PADDING_RIGHT = 3;
   public static final int PADDING_TOP = 3;
   public static final int PADDING_BOTTOM = 3;
   private static final int BACKGROUND_COLOR = -267386864;
   private static final int BORDER_COLOR_TOP = 1347420415;
   private static final int BORDER_COLOR_BOTTOM = 1344798847;

   public TooltipRenderUtil() {
      super();
   }

   public static void renderTooltipBackground(
      TooltipRenderUtil.BlitPainter var0, Matrix4f var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7
   ) {
      int var8 = var3 - 3;
      int var9 = var4 - 3;
      int var10 = var5 + 3 + 3;
      int var11 = var6 + 3 + 3;
      renderHorizontalLine(var0, var1, var2, var8, var9 - 1, var10, var7, -267386864);
      renderHorizontalLine(var0, var1, var2, var8, var9 + var11, var10, var7, -267386864);
      renderRectangle(var0, var1, var2, var8, var9, var10, var11, var7, -267386864);
      renderVerticalLine(var0, var1, var2, var8 - 1, var9, var11, var7, -267386864);
      renderVerticalLine(var0, var1, var2, var8 + var10, var9, var11, var7, -267386864);
      renderFrameGradient(var0, var1, var2, var8, var9 + 1, var10, var11, var7, 1347420415, 1344798847);
   }

   private static void renderFrameGradient(
      TooltipRenderUtil.BlitPainter var0, Matrix4f var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9
   ) {
      renderVerticalLineGradient(var0, var1, var2, var3, var4, var6 - 2, var7, var8, var9);
      renderVerticalLineGradient(var0, var1, var2, var3 + var5 - 1, var4, var6 - 2, var7, var8, var9);
      renderHorizontalLine(var0, var1, var2, var3, var4 - 1, var5, var7, var8);
      renderHorizontalLine(var0, var1, var2, var3, var4 - 1 + var6 - 1, var5, var7, var9);
   }

   private static void renderVerticalLine(
      TooltipRenderUtil.BlitPainter var0, Matrix4f var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7
   ) {
      var0.blit(var1, var2, var3, var4, var3 + 1, var4 + var5, var6, var7, var7);
   }

   private static void renderVerticalLineGradient(
      TooltipRenderUtil.BlitPainter var0, Matrix4f var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7, int var8
   ) {
      var0.blit(var1, var2, var3, var4, var3 + 1, var4 + var5, var6, var7, var8);
   }

   private static void renderHorizontalLine(
      TooltipRenderUtil.BlitPainter var0, Matrix4f var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7
   ) {
      var0.blit(var1, var2, var3, var4, var3 + var5, var4 + 1, var6, var7, var7);
   }

   private static void renderRectangle(
      TooltipRenderUtil.BlitPainter var0, Matrix4f var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7, int var8
   ) {
      var0.blit(var1, var2, var3, var4, var3 + var5, var4 + var6, var7, var8, var8);
   }

   @FunctionalInterface
   public interface BlitPainter {
      void blit(Matrix4f var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);
   }
}
