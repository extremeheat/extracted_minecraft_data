package net.minecraft.util;

import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.logging.log4j.Logger;

public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {
   private final Logger field_201709_a;

   public DefaultUncaughtExceptionHandler(Logger var1) {
      super();
      this.field_201709_a = var1;
   }

   public void uncaughtException(Thread var1, Throwable var2) {
      this.field_201709_a.error("Caught previously unhandled exception :");
      this.field_201709_a.error(var2);
   }
}
