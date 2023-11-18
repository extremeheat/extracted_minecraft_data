package net.minecraft.client.gui.components.debugchart;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.SampleLogger;

public abstract class AbstractDebugChart {
   protected static final int COLOR_GREY = 14737632;
   protected static final int CHART_HEIGHT = 60;
   protected static final int LINE_WIDTH = 1;
   protected final Font font;
   protected final SampleLogger logger;

   protected AbstractDebugChart(Font var1, SampleLogger var2) {
      super();
      this.font = var1;
      this.logger = var2;
   }

   public int getWidth(int var1) {
      return Math.min(this.logger.capacity() + 2, var1);
   }

   public void drawChart(GuiGraphics var1, int var2, int var3) {
      int var4 = var1.guiHeight();
      var1.fill(RenderType.guiOverlay(), var2, var4 - 60, var2 + var3, var4, -1873784752);
      long var5 = 0L;
      long var7 = 2147483647L;
      long var9 = -2147483648L;
      int var11 = Math.max(0, this.logger.capacity() - (var3 - 2));
      int var12 = this.logger.size() - var11;

      for(int var13 = 0; var13 < var12; ++var13) {
         int var14 = var2 + var13 + 1;
         long var15 = this.logger.get(var11 + var13);
         var7 = Math.min(var7, var15);
         var9 = Math.max(var9, var15);
         var5 += var15;
         int var17 = this.getSampleHeight((double)var15);
         int var18 = this.getSampleColor(var15);
         var1.fill(RenderType.guiOverlay(), var14, var4 - var17, var14 + 1, var4, var18);
      }

      var1.hLine(RenderType.guiOverlay(), var2, var2 + var3 - 1, var4 - 60, -1);
      var1.hLine(RenderType.guiOverlay(), var2, var2 + var3 - 1, var4 - 1, -1);
      var1.vLine(RenderType.guiOverlay(), var2, var4 - 60, var4, -1);
      var1.vLine(RenderType.guiOverlay(), var2 + var3 - 1, var4 - 60, var4, -1);
      if (var12 > 0) {
         String var19 = this.toDisplayString((double)var7) + " min";
         String var20 = this.toDisplayString((double)var5 / (double)var12) + " avg";
         String var21 = this.toDisplayString((double)var9) + " max";
         var1.drawString(this.font, var19, var2 + 2, var4 - 60 - 9, 14737632);
         var1.drawCenteredString(this.font, var20, var2 + var3 / 2, var4 - 60 - 9, 14737632);
         var1.drawString(this.font, var21, var2 + var3 - this.font.width(var21) - 2, var4 - 60 - 9, 14737632);
      }

      this.renderAdditionalLinesAndLabels(var1, var2, var3, var4);
   }

   protected void renderAdditionalLinesAndLabels(GuiGraphics var1, int var2, int var3, int var4) {
   }

   protected void drawStringWithShade(GuiGraphics var1, String var2, int var3, int var4) {
      var1.fill(RenderType.guiOverlay(), var3, var4, var3 + this.font.width(var2) + 1, var4 + 9, -1873784752);
      var1.drawString(this.font, var2, var3 + 1, var4 + 1, 14737632, false);
   }

   protected abstract String toDisplayString(double var1);

   protected abstract int getSampleHeight(double var1);

   protected abstract int getSampleColor(long var1);

   protected int getSampleColor(double var1, double var3, int var5, double var6, int var8, double var9, int var11) {
      var1 = Mth.clamp(var1, var3, var9);
      return var1 < var6
         ? FastColor.ARGB32.lerp((float)(var1 / (var6 - var3)), var5, var8)
         : FastColor.ARGB32.lerp((float)((var1 - var6) / (var9 - var6)), var8, var11);
   }
}
