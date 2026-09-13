package net.minecraft.crash;

import java.util.concurrent.Callable;

class CrashReport$4 implements Callable {
   CrashReport$4(CrashReport var1) {
      super();
      this.field_71492_a = var1;
   }

   public String call() {
      return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
   }
}
