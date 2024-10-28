package net.minecraft.client.gui.components.debugchart;

import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.debugchart.SampleStorage;

public abstract class AbstractDebugChart {
   protected static final int COLOR_GREY = 14737632;
   protected static final int CHART_HEIGHT = 60;
   protected static final int LINE_WIDTH = 1;
   protected final Font font;
   protected final SampleStorage sampleStorage;

   protected AbstractDebugChart(Font var1, SampleStorage var2) {
      super();
      this.font = var1;
      this.sampleStorage = var2;
   }

   public int getWidth(int var1) {
      return Math.min(this.sampleStorage.capacity() + 2, var1);
   }

   public void drawChart(GuiGraphics var1, int var2, int var3) {
      int var4 = var1.guiHeight();
      var1.fill(RenderType.guiOverlay(), var2, var4 - 60, var2 + var3, var4, -1873784752);
      long var5 = 0L;
      long var7 = 2147483647L;
      long var9 = -2147483648L;
      int var11 = Math.max(0, this.sampleStorage.capacity() - (var3 - 2));
      int var12 = this.sampleStorage.size() - var11;

      for(int var13 = 0; var13 < var12; ++var13) {
         int var14 = var2 + var13 + 1;
         int var15 = var11 + var13;
         long var16 = this.getValueForAggregation(var15);
         var7 = Math.min(var7, var16);
         var9 = Math.max(var9, var16);
         var5 += var16;
         this.drawDimensions(var1, var4, var14, var15);
      }

      var1.hLine(RenderType.guiOverlay(), var2, var2 + var3 - 1, var4 - 60, -1);
      var1.hLine(RenderType.guiOverlay(), var2, var2 + var3 - 1, var4 - 1, -1);
      var1.vLine(RenderType.guiOverlay(), var2, var4 - 60, var4, -1);
      var1.vLine(RenderType.guiOverlay(), var2 + var3 - 1, var4 - 60, var4, -1);
      if (var12 > 0) {
         String var10000 = this.toDisplayString((double)var7);
         String var18 = var10000 + " min";
         var10000 = this.toDisplayString((double)var5 / (double)var12);
         String var19 = var10000 + " avg";
         var10000 = this.toDisplayString((double)var9);
         String var20 = var10000 + " max";
         Font var10001 = this.font;
         int var10003 = var2 + 2;
         int var10004 = var4 - 60;
         Objects.requireNonNull(this.font);
         var1.drawString(var10001, var18, var10003, var10004 - 9, 14737632);
         var10001 = this.font;
         var10003 = var2 + var3 / 2;
         var10004 = var4 - 60;
         Objects.requireNonNull(this.font);
         var1.drawCenteredString(var10001, var19, var10003, var10004 - 9, 14737632);
         var10001 = this.font;
         var10003 = var2 + var3 - this.font.width(var20) - 2;
         var10004 = var4 - 60;
         Objects.requireNonNull(this.font);
         var1.drawString(var10001, var20, var10003, var10004 - 9, 14737632);
      }

      this.renderAdditionalLinesAndLabels(var1, var2, var3, var4);
   }

   protected void drawDimensions(GuiGraphics var1, int var2, int var3, int var4) {
      this.drawMainDimension(var1, var2, var3, var4);
      this.drawAdditionalDimensions(var1, var2, var3, var4);
   }

   protected void drawMainDimension(GuiGraphics var1, int var2, int var3, int var4) {
      long var5 = this.sampleStorage.get(var4);
      int var7 = this.getSampleHeight((double)var5);
      int var8 = this.getSampleColor(var5);
      var1.fill(RenderType.guiOverlay(), var3, var2 - var7, var3 + 1, var2, var8);
   }

   protected void drawAdditionalDimensions(GuiGraphics var1, int var2, int var3, int var4) {
   }

   protected long getValueForAggregation(int var1) {
      return this.sampleStorage.get(var1);
   }

   protected void renderAdditionalLinesAndLabels(GuiGraphics var1, int var2, int var3, int var4) {
   }

   protected void drawStringWithShade(GuiGraphics var1, String var2, int var3, int var4) {
      RenderType var10001 = RenderType.guiOverlay();
      int var10004 = var3 + this.font.width(var2) + 1;
      Objects.requireNonNull(this.font);
      var1.fill(var10001, var3, var4, var10004, var4 + 9, -1873784752);
      var1.drawString(this.font, var2, var3 + 1, var4 + 1, 14737632, false);
   }

   protected abstract String toDisplayString(double var1);

   protected abstract int getSampleHeight(double var1);

   protected abstract int getSampleColor(long var1);

   protected int getSampleColor(double var1, double var3, int var5, double var6, int var8, double var9, int var11) {
      var1 = Mth.clamp(var1, var3, var9);
      return var1 < var6 ? FastColor.ARGB32.lerp((float)((var1 - var3) / (var6 - var3)), var5, var8) : FastColor.ARGB32.lerp((float)((var1 - var6) / (var9 - var6)), var8, var11);
   }
}
