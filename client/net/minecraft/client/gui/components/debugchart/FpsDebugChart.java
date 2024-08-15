package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.debugchart.SampleStorage;

public class FpsDebugChart extends AbstractDebugChart {
   private static final int CHART_TOP_FPS = 30;
   private static final double CHART_TOP_VALUE = 33.333333333333336;

   public FpsDebugChart(Font var1, SampleStorage var2) {
      super(var1, var2);
   }

   @Override
   protected void renderAdditionalLinesAndLabels(GuiGraphics var1, int var2, int var3, int var4) {
      this.drawStringWithShade(var1, "30 FPS", var2 + 1, var4 - 60 + 1);
      this.drawStringWithShade(var1, "60 FPS", var2 + 1, var4 - 30 + 1);
      var1.hLine(RenderType.guiOverlay(), var2, var2 + var3 - 1, var4 - 30, -1);
      int var5 = Minecraft.getInstance().options.framerateLimit().get();
      if (var5 > 0 && var5 <= 250) {
         var1.hLine(RenderType.guiOverlay(), var2, var2 + var3 - 1, var4 - this.getSampleHeight(1.0E9 / (double)var5) - 1, -16711681);
      }
   }

   @Override
   protected String toDisplayString(double var1) {
      return String.format(Locale.ROOT, "%d ms", (int)Math.round(toMilliseconds(var1)));
   }

   @Override
   protected int getSampleHeight(double var1) {
      return (int)Math.round(toMilliseconds(var1) * 60.0 / 33.333333333333336);
   }

   @Override
   protected int getSampleColor(long var1) {
      return this.getSampleColor(toMilliseconds((double)var1), 0.0, -16711936, 28.0, -256, 56.0, -65536);
   }

   private static double toMilliseconds(double var0) {
      return var0 / 1000000.0;
   }
}
