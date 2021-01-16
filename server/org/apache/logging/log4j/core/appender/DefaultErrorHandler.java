package org.apache.logging.log4j.core.appender;

import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.status.StatusLogger;

public class DefaultErrorHandler implements ErrorHandler {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int MAX_EXCEPTIONS = 3;
   private static final long EXCEPTION_INTERVAL;
   private int exceptionCount = 0;
   private long lastException;
   private final Appender appender;

   public DefaultErrorHandler(Appender var1) {
      super();
      this.lastException = System.nanoTime() - EXCEPTION_INTERVAL - 1L;
      this.appender = var1;
   }

   public void error(String var1) {
      long var2 = System.nanoTime();
      if (var2 - this.lastException > EXCEPTION_INTERVAL || this.exceptionCount++ < 3) {
         LOGGER.error(var1);
      }

      this.lastException = var2;
   }

   public void error(String var1, Throwable var2) {
      long var3 = System.nanoTime();
      if (var3 - this.lastException > EXCEPTION_INTERVAL || this.exceptionCount++ < 3) {
         LOGGER.error(var1, var2);
      }

      this.lastException = var3;
      if (!this.appender.ignoreExceptions() && var2 != null && !(var2 instanceof AppenderLoggingException)) {
         throw new AppenderLoggingException(var1, var2);
      }
   }

   public void error(String var1, LogEvent var2, Throwable var3) {
      long var4 = System.nanoTime();
      if (var4 - this.lastException > EXCEPTION_INTERVAL || this.exceptionCount++ < 3) {
         LOGGER.error(var1, var3);
      }

      this.lastException = var4;
      if (!this.appender.ignoreExceptions() && var3 != null && !(var3 instanceof AppenderLoggingException)) {
         throw new AppenderLoggingException(var1, var3);
      }
   }

   public Appender getAppender() {
      return this.appender;
   }

   static {
      EXCEPTION_INTERVAL = TimeUnit.MINUTES.toNanos(5L);
   }
}
