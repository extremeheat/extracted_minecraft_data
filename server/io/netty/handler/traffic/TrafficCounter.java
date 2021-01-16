package io.netty.handler.traffic;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TrafficCounter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(TrafficCounter.class);
   private final AtomicLong currentWrittenBytes = new AtomicLong();
   private final AtomicLong currentReadBytes = new AtomicLong();
   private long writingTime;
   private long readingTime;
   private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
   private final AtomicLong cumulativeReadBytes = new AtomicLong();
   private long lastCumulativeTime;
   private long lastWriteThroughput;
   private long lastReadThroughput;
   final AtomicLong lastTime = new AtomicLong();
   private volatile long lastWrittenBytes;
   private volatile long lastReadBytes;
   private volatile long lastWritingTime;
   private volatile long lastReadingTime;
   private final AtomicLong realWrittenBytes = new AtomicLong();
   private long realWriteThroughput;
   final AtomicLong checkInterval = new AtomicLong(1000L);
   final String name;
   final AbstractTrafficShapingHandler trafficShapingHandler;
   final ScheduledExecutorService executor;
   Runnable monitor;
   volatile ScheduledFuture<?> scheduledFuture;
   volatile boolean monitorActive;

   public static long milliSecondFromNano() {
      return System.nanoTime() / 1000000L;
   }

   public synchronized void start() {
      if (!this.monitorActive) {
         this.lastTime.set(milliSecondFromNano());
         long var1 = this.checkInterval.get();
         if (var1 > 0L && this.executor != null) {
            this.monitorActive = true;
            this.monitor = new TrafficCounter.TrafficMonitoringTask();
            this.scheduledFuture = this.executor.schedule(this.monitor, var1, TimeUnit.MILLISECONDS);
         }

      }
   }

   public synchronized void stop() {
      if (this.monitorActive) {
         this.monitorActive = false;
         this.resetAccounting(milliSecondFromNano());
         if (this.trafficShapingHandler != null) {
            this.trafficShapingHandler.doAccounting(this);
         }

         if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
         }

      }
   }

   synchronized void resetAccounting(long var1) {
      long var3 = var1 - this.lastTime.getAndSet(var1);
      if (var3 != 0L) {
         if (logger.isDebugEnabled() && var3 > this.checkInterval() << 1) {
            logger.debug("Acct schedule not ok: " + var3 + " > 2*" + this.checkInterval() + " from " + this.name);
         }

         this.lastReadBytes = this.currentReadBytes.getAndSet(0L);
         this.lastWrittenBytes = this.currentWrittenBytes.getAndSet(0L);
         this.lastReadThroughput = this.lastReadBytes * 1000L / var3;
         this.lastWriteThroughput = this.lastWrittenBytes * 1000L / var3;
         this.realWriteThroughput = this.realWrittenBytes.getAndSet(0L) * 1000L / var3;
         this.lastWritingTime = Math.max(this.lastWritingTime, this.writingTime);
         this.lastReadingTime = Math.max(this.lastReadingTime, this.readingTime);
      }
   }

   public TrafficCounter(ScheduledExecutorService var1, String var2, long var3) {
      super();
      if (var2 == null) {
         throw new NullPointerException("name");
      } else {
         this.trafficShapingHandler = null;
         this.executor = var1;
         this.name = var2;
         this.init(var3);
      }
   }

   public TrafficCounter(AbstractTrafficShapingHandler var1, ScheduledExecutorService var2, String var3, long var4) {
      super();
      if (var1 == null) {
         throw new IllegalArgumentException("trafficShapingHandler");
      } else if (var3 == null) {
         throw new NullPointerException("name");
      } else {
         this.trafficShapingHandler = var1;
         this.executor = var2;
         this.name = var3;
         this.init(var4);
      }
   }

   private void init(long var1) {
      this.lastCumulativeTime = System.currentTimeMillis();
      this.writingTime = milliSecondFromNano();
      this.readingTime = this.writingTime;
      this.lastWritingTime = this.writingTime;
      this.lastReadingTime = this.writingTime;
      this.configure(var1);
   }

   public void configure(long var1) {
      long var3 = var1 / 10L * 10L;
      if (this.checkInterval.getAndSet(var3) != var3) {
         if (var3 <= 0L) {
            this.stop();
            this.lastTime.set(milliSecondFromNano());
         } else {
            this.start();
         }
      }

   }

   void bytesRecvFlowControl(long var1) {
      this.currentReadBytes.addAndGet(var1);
      this.cumulativeReadBytes.addAndGet(var1);
   }

   void bytesWriteFlowControl(long var1) {
      this.currentWrittenBytes.addAndGet(var1);
      this.cumulativeWrittenBytes.addAndGet(var1);
   }

   void bytesRealWriteFlowControl(long var1) {
      this.realWrittenBytes.addAndGet(var1);
   }

   public long checkInterval() {
      return this.checkInterval.get();
   }

   public long lastReadThroughput() {
      return this.lastReadThroughput;
   }

   public long lastWriteThroughput() {
      return this.lastWriteThroughput;
   }

   public long lastReadBytes() {
      return this.lastReadBytes;
   }

   public long lastWrittenBytes() {
      return this.lastWrittenBytes;
   }

   public long currentReadBytes() {
      return this.currentReadBytes.get();
   }

   public long currentWrittenBytes() {
      return this.currentWrittenBytes.get();
   }

   public long lastTime() {
      return this.lastTime.get();
   }

   public long cumulativeWrittenBytes() {
      return this.cumulativeWrittenBytes.get();
   }

   public long cumulativeReadBytes() {
      return this.cumulativeReadBytes.get();
   }

   public long lastCumulativeTime() {
      return this.lastCumulativeTime;
   }

   public AtomicLong getRealWrittenBytes() {
      return this.realWrittenBytes;
   }

   public long getRealWriteThroughput() {
      return this.realWriteThroughput;
   }

   public void resetCumulativeTime() {
      this.lastCumulativeTime = System.currentTimeMillis();
      this.cumulativeReadBytes.set(0L);
      this.cumulativeWrittenBytes.set(0L);
   }

   public String name() {
      return this.name;
   }

   /** @deprecated */
   @Deprecated
   public long readTimeToWait(long var1, long var3, long var5) {
      return this.readTimeToWait(var1, var3, var5, milliSecondFromNano());
   }

   public long readTimeToWait(long var1, long var3, long var5, long var7) {
      this.bytesRecvFlowControl(var1);
      if (var1 != 0L && var3 != 0L) {
         long var9 = this.lastTime.get();
         long var11 = this.currentReadBytes.get();
         long var13 = this.readingTime;
         long var15 = this.lastReadBytes;
         long var17 = var7 - var9;
         long var19 = Math.max(this.lastReadingTime - var9, 0L);
         long var21;
         if (var17 > 10L) {
            var21 = var11 * 1000L / var3 - var17 + var19;
            if (var21 > 10L) {
               if (logger.isDebugEnabled()) {
                  logger.debug("Time: " + var21 + ':' + var11 + ':' + var17 + ':' + var19);
               }

               if (var21 > var5 && var7 + var21 - var13 > var5) {
                  var21 = var5;
               }

               this.readingTime = Math.max(var13, var7 + var21);
               return var21;
            } else {
               this.readingTime = Math.max(var13, var7);
               return 0L;
            }
         } else {
            var21 = var11 + var15;
            long var23 = var17 + this.checkInterval.get();
            long var25 = var21 * 1000L / var3 - var23 + var19;
            if (var25 > 10L) {
               if (logger.isDebugEnabled()) {
                  logger.debug("Time: " + var25 + ':' + var21 + ':' + var23 + ':' + var19);
               }

               if (var25 > var5 && var7 + var25 - var13 > var5) {
                  var25 = var5;
               }

               this.readingTime = Math.max(var13, var7 + var25);
               return var25;
            } else {
               this.readingTime = Math.max(var13, var7);
               return 0L;
            }
         }
      } else {
         return 0L;
      }
   }

   /** @deprecated */
   @Deprecated
   public long writeTimeToWait(long var1, long var3, long var5) {
      return this.writeTimeToWait(var1, var3, var5, milliSecondFromNano());
   }

   public long writeTimeToWait(long var1, long var3, long var5, long var7) {
      this.bytesWriteFlowControl(var1);
      if (var1 != 0L && var3 != 0L) {
         long var9 = this.lastTime.get();
         long var11 = this.currentWrittenBytes.get();
         long var13 = this.lastWrittenBytes;
         long var15 = this.writingTime;
         long var17 = Math.max(this.lastWritingTime - var9, 0L);
         long var19 = var7 - var9;
         long var21;
         if (var19 > 10L) {
            var21 = var11 * 1000L / var3 - var19 + var17;
            if (var21 > 10L) {
               if (logger.isDebugEnabled()) {
                  logger.debug("Time: " + var21 + ':' + var11 + ':' + var19 + ':' + var17);
               }

               if (var21 > var5 && var7 + var21 - var15 > var5) {
                  var21 = var5;
               }

               this.writingTime = Math.max(var15, var7 + var21);
               return var21;
            } else {
               this.writingTime = Math.max(var15, var7);
               return 0L;
            }
         } else {
            var21 = var11 + var13;
            long var23 = var19 + this.checkInterval.get();
            long var25 = var21 * 1000L / var3 - var23 + var17;
            if (var25 > 10L) {
               if (logger.isDebugEnabled()) {
                  logger.debug("Time: " + var25 + ':' + var21 + ':' + var23 + ':' + var17);
               }

               if (var25 > var5 && var7 + var25 - var15 > var5) {
                  var25 = var5;
               }

               this.writingTime = Math.max(var15, var7 + var25);
               return var25;
            } else {
               this.writingTime = Math.max(var15, var7);
               return 0L;
            }
         }
      } else {
         return 0L;
      }
   }

   public String toString() {
      return (new StringBuilder(165)).append("Monitor ").append(this.name).append(" Current Speed Read: ").append(this.lastReadThroughput >> 10).append(" KB/s, ").append("Asked Write: ").append(this.lastWriteThroughput >> 10).append(" KB/s, ").append("Real Write: ").append(this.realWriteThroughput >> 10).append(" KB/s, ").append("Current Read: ").append(this.currentReadBytes.get() >> 10).append(" KB, ").append("Current asked Write: ").append(this.currentWrittenBytes.get() >> 10).append(" KB, ").append("Current real Write: ").append(this.realWrittenBytes.get() >> 10).append(" KB").toString();
   }

   private final class TrafficMonitoringTask implements Runnable {
      private TrafficMonitoringTask() {
         super();
      }

      public void run() {
         if (TrafficCounter.this.monitorActive) {
            TrafficCounter.this.resetAccounting(TrafficCounter.milliSecondFromNano());
            if (TrafficCounter.this.trafficShapingHandler != null) {
               TrafficCounter.this.trafficShapingHandler.doAccounting(TrafficCounter.this);
            }

            TrafficCounter.this.scheduledFuture = TrafficCounter.this.executor.schedule(this, TrafficCounter.this.checkInterval.get(), TimeUnit.MILLISECONDS);
         }
      }

      // $FF: synthetic method
      TrafficMonitoringTask(Object var2) {
         this();
      }
   }
}
