package com.mojang.realmsclient.exception;

import org.slf4j.Logger;

public class RealmsDefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
   private final Logger logger;

   public RealmsDefaultUncaughtExceptionHandler(final Logger logger) {
      super();
      this.logger = logger;
   }

   public void uncaughtException(final Thread t, final Throwable e) {
      this.logger.error("Caught previously unhandled exception", e);
   }
}
