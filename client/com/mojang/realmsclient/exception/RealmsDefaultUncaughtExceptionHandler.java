package com.mojang.realmsclient.exception;

import org.slf4j.Logger;

public class RealmsDefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
   private final Logger logger;

   public RealmsDefaultUncaughtExceptionHandler(Logger var1) {
      super();
      this.logger = var1;
   }

   public void uncaughtException(Thread var1, Throwable var2) {
      this.logger.error("Caught previously unhandled exception", var2);
   }
}
