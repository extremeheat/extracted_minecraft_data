package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.debugchart.SampleStorage;
import net.minecraft.util.debugchart.TpsDebugDimensions;

public class TpsDebugChart extends AbstractDebugChart {
   private static final int RED = -65536;
   private static final int YELLOW = -256;
   private static final int GREEN = -16711936;
   private static final int TICK_METHOD_COLOR = -6745839;
   private static final int TASK_COLOR = -4548257;
   private static final int OTHER_COLOR = -10547572;
   private final Supplier<Float> msptSupplier;

   public TpsDebugChart(Font var1, SampleStorage var2, Supplier<Float> var3) {
      super(var1, var2);
      this.msptSupplier = var3;
   }

   @Override
   protected void renderAdditionalLinesAndLabels(GuiGraphics var1, int var2, int var3, int var4) {
      float var5 = (float)TimeUtil.MILLISECONDS_PER_SECOND / this.msptSupplier.get();
      this.drawStringWithShade(var1, String.format("%.1f TPS", var5), var2 + 1, var4 - 60 + 1);
   }

   @Override
   protected void drawAdditionalDimensions(GuiGraphics var1, int var2, int var3, int var4) {
      long var5 = this.sampleStorage.get(var4, TpsDebugDimensions.TICK_SERVER_METHOD.ordinal());
      int var7 = this.getSampleHeight((double)var5);
      var1.fill(RenderType.guiOverlay(), var3, var2 - var7, var3 + 1, var2, -6745839);
      long var8 = this.sampleStorage.get(var4, TpsDebugDimensions.SCHEDULED_TASKS.ordinal());
      int var10 = this.getSampleHeight((double)var8);
      var1.fill(RenderType.guiOverlay(), var3, var2 - var7 - var10, var3 + 1, var2 - var7, -4548257);
      long var11 = this.sampleStorage.get(var4) - this.sampleStorage.get(var4, TpsDebugDimensions.IDLE.ordinal()) - var5 - var8;
      int var13 = this.getSampleHeight((double)var11);
      var1.fill(RenderType.guiOverlay(), var3, var2 - var13 - var10 - var7, var3 + 1, var2 - var10 - var7, -10547572);
   }

   @Override
   protected long getValueForAggregation(int var1) {
      return this.sampleStorage.get(var1) - this.sampleStorage.get(var1, TpsDebugDimensions.IDLE.ordinal());
   }

   @Override
   protected String toDisplayString(double var1) {
      return String.format(Locale.ROOT, "%d ms", (int)Math.round(toMilliseconds(var1)));
   }

   @Override
   protected int getSampleHeight(double var1) {
      return (int)Math.round(toMilliseconds(var1) * 60.0 / (double)this.msptSupplier.get().floatValue());
   }

   @Override
   protected int getSampleColor(long var1) {
      float var3 = this.msptSupplier.get();
      return this.getSampleColor(toMilliseconds((double)var1), (double)var3, -16711936, (double)var3 * 1.125, -256, (double)var3 * 1.25, -65536);
   }

   private static double toMilliseconds(double var0) {
      return var0 / 1000000.0;
   }
}
