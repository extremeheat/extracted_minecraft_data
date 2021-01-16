package org.apache.logging.log4j.core.config;

import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Supplier;

public class LockingReliabilityStrategy implements ReliabilityStrategy {
   private final LoggerConfig loggerConfig;
   private final ReadWriteLock reconfigureLock = new ReentrantReadWriteLock();
   private volatile boolean isStopping = false;

   public LockingReliabilityStrategy(LoggerConfig var1) {
      super();
      this.loggerConfig = (LoggerConfig)Objects.requireNonNull(var1, "loggerConfig was null");
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
      this.reconfigureLock.readLock().lock();
      if (this.isStopping) {
         this.reconfigureLock.readLock().unlock();
         return false;
      } else {
         return true;
      }
   }

   public void afterLogEvent() {
      this.reconfigureLock.readLock().unlock();
   }

   public void beforeStopAppenders() {
      this.reconfigureLock.writeLock().lock();

      try {
         this.isStopping = true;
      } finally {
         this.reconfigureLock.writeLock().unlock();
      }

   }

   public void beforeStopConfiguration(Configuration var1) {
   }
}
