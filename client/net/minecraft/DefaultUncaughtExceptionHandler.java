package net.minecraft;

import org.slf4j.Logger;

public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
   private final Logger logger;

   public DefaultUncaughtExceptionHandler(final Logger logger) {
      super();
      this.logger = logger;
   }

   public void uncaughtException(final Thread t, final Throwable e) {
      this.logger.error("Caught previously unhandled exception :", e);
   }
}
