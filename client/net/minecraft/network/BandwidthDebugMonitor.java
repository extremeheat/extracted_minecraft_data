package net.minecraft.network;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.debugchart.LocalSampleLogger;

public class BandwidthDebugMonitor {
   private final AtomicInteger bytesReceived = new AtomicInteger();
   private final LocalSampleLogger bandwidthLogger;

   public BandwidthDebugMonitor(final LocalSampleLogger bandwidthLogger) {
      super();
      this.bandwidthLogger = bandwidthLogger;
   }

   public void onReceive(final int bytes) {
      this.bytesReceived.getAndAdd(bytes);
   }

   public void tick() {
      this.bandwidthLogger.logSample((long)this.bytesReceived.getAndSet(0));
   }
}
