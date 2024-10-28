package net.minecraft.client.telemetry.events;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;

public final class PerformanceMetricsEvent extends AggregatedTelemetryEvent {
   private static final long DEDICATED_MEMORY_KB = toKilobytes(Runtime.getRuntime().maxMemory());
   private final LongList fpsSamples = new LongArrayList();
   private final LongList frameTimeSamples = new LongArrayList();
   private final LongList usedMemorySamples = new LongArrayList();

   public PerformanceMetricsEvent() {
      super();
   }

   public void tick(TelemetryEventSender var1) {
      if (Minecraft.getInstance().telemetryOptInExtra()) {
         super.tick(var1);
      }

   }

   private void resetValues() {
      this.fpsSamples.clear();
      this.frameTimeSamples.clear();
      this.usedMemorySamples.clear();
   }

   public void takeSample() {
      this.fpsSamples.add((long)Minecraft.getInstance().getFps());
      this.takeUsedMemorySample();
      this.frameTimeSamples.add(Minecraft.getInstance().getFrameTimeNs());
   }

   private void takeUsedMemorySample() {
      long var1 = Runtime.getRuntime().totalMemory();
      long var3 = Runtime.getRuntime().freeMemory();
      long var5 = var1 - var3;
      this.usedMemorySamples.add(toKilobytes(var5));
   }

   public void sendEvent(TelemetryEventSender var1) {
      var1.send(TelemetryEventType.PERFORMANCE_METRICS, (var1x) -> {
         var1x.put(TelemetryProperty.FRAME_RATE_SAMPLES, new LongArrayList(this.fpsSamples));
         var1x.put(TelemetryProperty.RENDER_TIME_SAMPLES, new LongArrayList(this.frameTimeSamples));
         var1x.put(TelemetryProperty.USED_MEMORY_SAMPLES, new LongArrayList(this.usedMemorySamples));
         var1x.put(TelemetryProperty.NUMBER_OF_SAMPLES, this.getSampleCount());
         var1x.put(TelemetryProperty.RENDER_DISTANCE, Minecraft.getInstance().options.getEffectiveRenderDistance());
         var1x.put(TelemetryProperty.DEDICATED_MEMORY_KB, (int)DEDICATED_MEMORY_KB);
      });
      this.resetValues();
   }

   private static long toKilobytes(long var0) {
      return var0 / 1000L;
   }
}
