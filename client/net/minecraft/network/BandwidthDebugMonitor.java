package net.minecraft.network;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.debugchart.LocalSampleLogger;

public class BandwidthDebugMonitor {
   private final AtomicInteger bytesReceived = new AtomicInteger();
   private final LocalSampleLogger bandwidthLogger;

   public BandwidthDebugMonitor(LocalSampleLogger var1) {
      super();
      this.bandwidthLogger = var1;
   }

   public void onReceive(int var1) {
      this.bytesReceived.getAndAdd(var1);
   }

   public void tick() {
      this.bandwidthLogger.logSample((long)this.bytesReceived.getAndSet(0));
   }
}
