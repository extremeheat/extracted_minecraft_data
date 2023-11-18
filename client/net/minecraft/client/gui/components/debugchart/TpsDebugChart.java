package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.SampleLogger;

public class TpsDebugChart extends AbstractDebugChart {
   private static final int RED = -65536;
   private static final int YELLOW = -256;
   private static final int GREEN = -16711936;
   private static final int CHART_TOP_VALUE = 50;

   public TpsDebugChart(Font var1, SampleLogger var2) {
      super(var1, var2);
   }

   @Override
   protected void renderAdditionalLinesAndLabels(GuiGraphics var1, int var2, int var3, int var4) {
      this.drawStringWithShade(var1, "20 TPS", var2 + 1, var4 - 60 + 1);
   }

   @Override
   protected String toDisplayString(double var1) {
      return String.format(Locale.ROOT, "%d ms", (int)Math.round(toMilliseconds(var1)));
   }

   @Override
   protected int getSampleHeight(double var1) {
      return (int)Math.round(toMilliseconds(var1) * 60.0 / 50.0);
   }

   @Override
   protected int getSampleColor(long var1) {
      return this.getSampleColor(toMilliseconds((double)var1), 0.0, -16711936, 25.0, -256, 50.0, -65536);
   }

   private static double toMilliseconds(double var0) {
      return var0 / 1000000.0;
   }
}
