package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.debugchart.SampleStorage;

public class PingDebugChart extends AbstractDebugChart {
   private static final int CHART_TOP_VALUE = 500;

   public PingDebugChart(Font var1, SampleStorage var2) {
      super(var1, var2);
   }

   @Override
   protected void renderAdditionalLinesAndLabels(GuiGraphics var1, int var2, int var3, int var4) {
      this.drawStringWithShade(var1, "500 ms", var2 + 1, var4 - 60 + 1);
   }

   @Override
   protected String toDisplayString(double var1) {
      return String.format(Locale.ROOT, "%d ms", (int)Math.round(var1));
   }

   @Override
   protected int getSampleHeight(double var1) {
      return (int)Math.round(var1 * 60.0 / 500.0);
   }

   @Override
   protected int getSampleColor(long var1) {
      return this.getSampleColor((double)var1, 0.0, -16711936, 250.0, -256, 500.0, -65536);
   }
}
