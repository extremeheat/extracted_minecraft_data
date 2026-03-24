package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.debugchart.SampleStorage;

public class FpsDebugChart extends AbstractDebugChart {
   private static final int CHART_TOP_FPS = 30;
   private static final double CHART_TOP_VALUE = 33.333333333333336;

   public FpsDebugChart(final Font font, final SampleStorage sampleStorage) {
      super(font, sampleStorage);
   }

   protected void extractAdditionalLinesAndLabels(final GuiGraphicsExtractor graphics, final int left, final int width, final int bottom) {
      this.extractStringWithShade(graphics, "30 FPS", left + 1, bottom - 60 + 1);
      this.extractStringWithShade(graphics, "60 FPS", left + 1, bottom - 30 + 1);
      graphics.horizontalLine(left, left + width - 1, bottom - 30, -1);
      int framerateLimit = (Integer)Minecraft.getInstance().options.framerateLimit().get();
      if (framerateLimit > 0 && framerateLimit <= 250) {
         graphics.horizontalLine(left, left + width - 1, bottom - this.getSampleHeight(1.0E9 / (double)framerateLimit) - 1, -16711681);
      }

   }

   protected String toDisplayString(final double nanos) {
      return String.format(Locale.ROOT, "%d ms", (int)Math.round(toMilliseconds(nanos)));
   }

   protected int getSampleHeight(final double nanos) {
      return (int)Math.round(toMilliseconds(nanos) * 60.0 / 33.333333333333336);
   }

   protected int getSampleColor(final long nanos) {
      return this.getSampleColor(toMilliseconds((double)nanos), 0.0, -16711936, 28.0, -256, 56.0, -65536);
   }

   private static double toMilliseconds(final double nanos) {
      return nanos / 1000000.0;
   }
}
