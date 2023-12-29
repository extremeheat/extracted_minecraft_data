package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.SampleLogger;
import net.minecraft.util.TimeUtil;

public class TpsDebugChart extends AbstractDebugChart {
   private static final int RED = -65536;
   private static final int YELLOW = -256;
   private static final int GREEN = -16711936;
   private final Supplier<Float> msptSupplier;

   public TpsDebugChart(Font var1, SampleLogger var2, Supplier<Float> var3) {
      super(var1, var2);
      this.msptSupplier = var3;
   }

   @Override
   protected void renderAdditionalLinesAndLabels(GuiGraphics var1, int var2, int var3, int var4) {
      float var5 = (float)TimeUtil.MILLISECONDS_PER_SECOND / this.msptSupplier.get();
      this.drawStringWithShade(var1, String.format("%.1f TPS", var5), var2 + 1, var4 - 60 + 1);
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
      return this.getSampleColor(toMilliseconds((double)var1), 0.0, -16711936, (double)var3 / 2.0, -256, (double)var3, -65536);
   }

   private static double toMilliseconds(double var0) {
      return var0 / 1000000.0;
   }
}
