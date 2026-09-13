package net.minecraft.crash;

import java.util.concurrent.Callable;

class CrashReport$3 implements Callable {
   CrashReport$3(CrashReport var1) {
      super();
      this.field_71490_a = var1;
   }

   public String call() {
      return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
   }
}
