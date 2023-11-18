package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.GuiGraphics;

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

   public static void renderTooltipBackground(GuiGraphics var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = var1 - 3;
      int var7 = var2 - 3;
      int var8 = var3 + 3 + 3;
      int var9 = var4 + 3 + 3;
      renderHorizontalLine(var0, var6, var7 - 1, var8, var5, -267386864);
      renderHorizontalLine(var0, var6, var7 + var9, var8, var5, -267386864);
      renderRectangle(var0, var6, var7, var8, var9, var5, -267386864);
      renderVerticalLine(var0, var6 - 1, var7, var9, var5, -267386864);
      renderVerticalLine(var0, var6 + var8, var7, var9, var5, -267386864);
      renderFrameGradient(var0, var6, var7 + 1, var8, var9, var5, 1347420415, 1344798847);
   }

   private static void renderFrameGradient(GuiGraphics var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      renderVerticalLineGradient(var0, var1, var2, var4 - 2, var5, var6, var7);
      renderVerticalLineGradient(var0, var1 + var3 - 1, var2, var4 - 2, var5, var6, var7);
      renderHorizontalLine(var0, var1, var2 - 1, var3, var5, var6);
      renderHorizontalLine(var0, var1, var2 - 1 + var4 - 1, var3, var5, var7);
   }

   private static void renderVerticalLine(GuiGraphics var0, int var1, int var2, int var3, int var4, int var5) {
      var0.fill(var1, var2, var1 + 1, var2 + var3, var4, var5);
   }

   private static void renderVerticalLineGradient(GuiGraphics var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      var0.fillGradient(var1, var2, var1 + 1, var2 + var3, var4, var5, var6);
   }

   private static void renderHorizontalLine(GuiGraphics var0, int var1, int var2, int var3, int var4, int var5) {
      var0.fill(var1, var2, var1 + var3, var2 + 1, var4, var5);
   }

   private static void renderRectangle(GuiGraphics var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      var0.fill(var1, var2, var1 + var3, var2 + var4, var5, var6);
   }
}
