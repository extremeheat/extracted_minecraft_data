package net.minecraft;

import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.logging.log4j.Logger;

public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {
   private final Logger logger;

   public DefaultUncaughtExceptionHandler(Logger var1) {
      super();
      this.logger = var1;
   }

   public void uncaughtException(Thread var1, Throwable var2) {
      this.logger.error("Caught previously unhandled exception :", var2);
   }
}
