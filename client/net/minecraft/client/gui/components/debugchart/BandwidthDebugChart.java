package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.util.SampleLogger;

public class BandwidthDebugChart extends AbstractDebugChart {
   private static final int MIN_COLOR = -16711681;
   private static final int MID_COLOR = -6250241;
   private static final int MAX_COLOR = -65536;
   private static final int KILOBYTE = 1024;
   private static final int MEGABYTE = 1048576;
   private static final int CHART_TOP_VALUE = 1048576;

   public BandwidthDebugChart(Font var1, SampleLogger var2) {
      super(var1, var2);
   }

   @Override
   protected void renderAdditionalLinesAndLabels(GuiGraphics var1, int var2, int var3, int var4) {
      this.drawLabeledLineAtValue(var1, var2, var3, var4, 64);
      this.drawLabeledLineAtValue(var1, var2, var3, var4, 1024);
      this.drawLabeledLineAtValue(var1, var2, var3, var4, 16384);
      this.drawStringWithShade(var1, toDisplayStringInternal(1048576.0), var2 + 1, var4 - getSampleHeightInternal(1048576.0) + 1);
   }

   private void drawLabeledLineAtValue(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      this.drawLineWithLabel(var1, var2, var3, var4 - getSampleHeightInternal((double)var5), toDisplayStringInternal((double)var5));
   }

   private void drawLineWithLabel(GuiGraphics var1, int var2, int var3, int var4, String var5) {
      this.drawStringWithShade(var1, var5, var2 + 1, var4 + 1);
      var1.hLine(RenderType.guiOverlay(), var2, var2 + var3 - 1, var4, -1);
   }

   @Override
   protected String toDisplayString(double var1) {
      return toDisplayStringInternal(toBytesPerSecond(var1));
   }

   private static String toDisplayStringInternal(double var0) {
      if (var0 >= 1048576.0) {
         return String.format(Locale.ROOT, "%.1f MiB/s", var0 / 1048576.0);
      } else {
         return var0 >= 1024.0 ? String.format(Locale.ROOT, "%.1f KiB/s", var0 / 1024.0) : String.format(Locale.ROOT, "%d B/s", Mth.floor(var0));
      }
   }

   @Override
   protected int getSampleHeight(double var1) {
      return getSampleHeightInternal(toBytesPerSecond(var1));
   }

   private static int getSampleHeightInternal(double var0) {
      return (int)Math.round(Math.log(var0 + 1.0) * 60.0 / Math.log(1048576.0));
   }

   @Override
   protected int getSampleColor(long var1) {
      return this.getSampleColor(toBytesPerSecond((double)var1), 0.0, -16711681, 8192.0, -6250241, 1.048576E7, -65536);
   }

   private static double toBytesPerSecond(double var0) {
      return var0 * 20.0;
   }
}
