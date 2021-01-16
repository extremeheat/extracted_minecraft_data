package org.apache.logging.log4j.core.config;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Supplier;

public class AwaitCompletionReliabilityStrategy implements ReliabilityStrategy {
   private static final int MAX_RETRIES = 3;
   private final AtomicInteger counter = new AtomicInteger();
   private final AtomicBoolean shutdown = new AtomicBoolean(false);
   private final Lock shutdownLock = new ReentrantLock();
   private final Condition noLogEvents;
   private final LoggerConfig loggerConfig;

   public AwaitCompletionReliabilityStrategy(LoggerConfig var1) {
      super();
      this.noLogEvents = this.shutdownLock.newCondition();
      this.loggerConfig = (LoggerConfig)Objects.requireNonNull(var1, "loggerConfig is null");
   }

   public void log(Supplier<LoggerConfig> var1, String var2, String var3, Marker var4, Level var5, Message var6, Throwable var7) {
      LoggerConfig var8 = this.getActiveLoggerConfig(var1);

      try {
         var8.log(var2, var3, var4, var5, var6, var7);
      } finally {
         var8.getReliabilityStrategy().afterLogEvent();
      }

   }

   public void log(Supplier<LoggerConfig> var1, LogEvent var2) {
      LoggerConfig var3 = this.getActiveLoggerConfig(var1);

      try {
         var3.log(var2);
      } finally {
         var3.getReliabilityStrategy().afterLogEvent();
      }

   }

   public LoggerConfig getActiveLoggerConfig(Supplier<LoggerConfig> var1) {
      LoggerConfig var2 = this.loggerConfig;
      if (!this.beforeLogEvent()) {
         var2 = (LoggerConfig)var1.get();
         return var2.getReliabilityStrategy().getActiveLoggerConfig(var1);
      } else {
         return var2;
      }
   }

   private boolean beforeLogEvent() {
      return this.counter.incrementAndGet() > 0;
   }

   public void afterLogEvent() {
      if (this.counter.decrementAndGet() == 0 && this.shutdown.get()) {
         this.signalCompletionIfShutdown();
      }

   }

   private void signalCompletionIfShutdown() {
      Lock var1 = this.shutdownLock;
      var1.lock();

      try {
         this.noLogEvents.signalAll();
      } finally {
         var1.unlock();
      }

   }

   public void beforeStopAppenders() {
      this.waitForCompletion();
   }

   private void waitForCompletion() {
      this.shutdownLock.lock();

      try {
         if (this.shutdown.compareAndSet(false, true)) {
            int var1 = 0;

            while(!this.counter.compareAndSet(0, -2147483648)) {
               if (this.counter.get() < 0) {
                  return;
               }

               try {
                  this.noLogEvents.await((long)(var1 + 1), TimeUnit.SECONDS);
               } catch (InterruptedException var6) {
                  ++var1;
                  if (var1 > 3) {
                     return;
                  }
               }
            }

         }
      } finally {
         this.shutdownLock.unlock();
      }
   }

   public void beforeStopConfiguration(Configuration var1) {
   }
}
