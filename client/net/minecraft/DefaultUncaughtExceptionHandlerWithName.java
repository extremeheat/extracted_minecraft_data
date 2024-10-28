package net.minecraft;

import org.slf4j.Logger;

public class DefaultUncaughtExceptionHandlerWithName implements Thread.UncaughtExceptionHandler {
   private final Logger logger;

   public DefaultUncaughtExceptionHandlerWithName(Logger var1) {
      super();
      this.logger = var1;
   }

   public void uncaughtException(Thread var1, Throwable var2) {
      this.logger.error("Caught previously unhandled exception :");
      this.logger.error(var1.getName(), var2);
   }
}
