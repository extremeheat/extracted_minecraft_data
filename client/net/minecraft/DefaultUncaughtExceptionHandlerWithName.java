package net.minecraft;

import java.lang.Thread.UncaughtExceptionHandler;
import org.slf4j.Logger;

public class DefaultUncaughtExceptionHandlerWithName implements UncaughtExceptionHandler {
   private final Logger logger;

   public DefaultUncaughtExceptionHandlerWithName(Logger var1) {
      super();
      this.logger = var1;
   }

   @Override
   public void uncaughtException(Thread var1, Throwable var2) {
      this.logger.error("Caught previously unhandled exception :");
      this.logger.error(var1.getName(), var2);
   }
}
