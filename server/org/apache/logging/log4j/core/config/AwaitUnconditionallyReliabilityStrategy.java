package org.apache.logging.log4j.core.config;

import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Supplier;

public class AwaitUnconditionallyReliabilityStrategy implements ReliabilityStrategy {
   private static final long DEFAULT_SLEEP_MILLIS = 5000L;
   private static final long SLEEP_MILLIS = sleepMillis();
   private final LoggerConfig loggerConfig;

   public AwaitUnconditionallyReliabilityStrategy(LoggerConfig var1) {
      super();
      this.loggerConfig = (LoggerConfig)Objects.requireNonNull(var1, "loggerConfig is null");
   }

   private static long sleepMillis() {
      return PropertiesUtil.getProperties().getLongProperty("log4j.waitMillisBeforeStopOldConfig", 5000L);
   }

   public void log(Supplier<LoggerConfig> var1, String var2, String var3, Marker var4, Level var5, Message var6, Throwable var7) {
      this.loggerConfig.log(var2, var3, var4, var5, var6, var7);
   }

   public void log(Supplier<LoggerConfig> var1, LogEvent var2) {
      this.loggerConfig.log(var2);
   }

   public LoggerConfig getActiveLoggerConfig(Supplier<LoggerConfig> var1) {
      return this.loggerConfig;
   }

   public void afterLogEvent() {
   }

   public void beforeStopAppenders() {
   }

   public void beforeStopConfiguration(Configuration var1) {
      if (this.loggerConfig == var1.getRootLogger()) {
         try {
            Thread.sleep(SLEEP_MILLIS);
         } catch (InterruptedException var3) {
            StatusLogger.getLogger().warn("Sleep before stop configuration was interrupted.");
         }
      }

   }
}
