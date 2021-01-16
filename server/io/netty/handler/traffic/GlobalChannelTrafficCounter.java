package io.netty.handler.traffic;

import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GlobalChannelTrafficCounter extends TrafficCounter {
   public GlobalChannelTrafficCounter(GlobalChannelTrafficShapingHandler var1, ScheduledExecutorService var2, String var3, long var4) {
      super(var1, var2, var3, var4);
      if (var2 == null) {
         throw new IllegalArgumentException("Executor must not be null");
      }
   }

   public synchronized void start() {
      if (!this.monitorActive) {
         this.lastTime.set(milliSecondFromNano());
         long var1 = this.checkInterval.get();
         if (var1 > 0L) {
            this.monitorActive = true;
            this.monitor = new GlobalChannelTrafficCounter.MixedTrafficMonitoringTask((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler, this);
            this.scheduledFuture = this.executor.schedule(this.monitor, var1, TimeUnit.MILLISECONDS);
         }

      }
   }

   public synchronized void stop() {
      if (this.monitorActive) {
         this.monitorActive = false;
         this.resetAccounting(milliSecondFromNano());
         this.trafficShapingHandler.doAccounting(this);
         if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
         }

      }
   }

   public void resetCumulativeTime() {
      Iterator var1 = ((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler).channelQueues.values().iterator();

      while(var1.hasNext()) {
         GlobalChannelTrafficShapingHandler.PerChannel var2 = (GlobalChannelTrafficShapingHandler.PerChannel)var1.next();
         var2.channelTrafficCounter.resetCumulativeTime();
      }

      super.resetCumulativeTime();
   }

   private static class MixedTrafficMonitoringTask implements Runnable {
      private final GlobalChannelTrafficShapingHandler trafficShapingHandler1;
      private final TrafficCounter counter;

      MixedTrafficMonitoringTask(GlobalChannelTrafficShapingHandler var1, TrafficCounter var2) {
         super();
         this.trafficShapingHandler1 = var1;
         this.counter = var2;
      }

      public void run() {
         if (this.counter.monitorActive) {
            long var1 = TrafficCounter.milliSecondFromNano();
            this.counter.resetAccounting(var1);
            Iterator var3 = this.trafficShapingHandler1.channelQueues.values().iterator();

            while(var3.hasNext()) {
               GlobalChannelTrafficShapingHandler.PerChannel var4 = (GlobalChannelTrafficShapingHandler.PerChannel)var3.next();
               var4.channelTrafficCounter.resetAccounting(var1);
            }

            this.trafficShapingHandler1.doAccounting(this.counter);
            this.counter.scheduledFuture = this.counter.executor.schedule(this, this.counter.checkInterval.get(), TimeUnit.MILLISECONDS);
         }
      }
   }
}
