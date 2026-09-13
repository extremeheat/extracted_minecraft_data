package net.minecraft.crash;

import java.util.concurrent.Callable;

class CrashReport$2 implements Callable {
   CrashReport$2(CrashReport var1) {
      super();
      this.field_71496_a = var1;
   }

   public String call() {
      return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
   }
}
