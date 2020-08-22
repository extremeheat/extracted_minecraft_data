package com.mojang.realmsclient.exception;

import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.logging.log4j.Logger;

public class RealmsDefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {
   private final Logger logger;

   public RealmsDefaultUncaughtExceptionHandler(Logger var1) {
      this.logger = var1;
   }

   public void uncaughtException(Thread var1, Throwable var2) {
      this.logger.error("Caught previously unhandled exception :");
      this.logger.error(var2);
   }
}
