package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Supplier;

public interface ReliabilityStrategy {
   void log(Supplier<LoggerConfig> var1, String var2, String var3, Marker var4, Level var5, Message var6, Throwable var7);

   void log(Supplier<LoggerConfig> var1, LogEvent var2);

   LoggerConfig getActiveLoggerConfig(Supplier<LoggerConfig> var1);

   void afterLogEvent();

   void beforeStopAppenders();

   void beforeStopConfiguration(Configuration var1);
}
