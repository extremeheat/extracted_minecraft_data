package net.minecraft.crash;

import java.util.concurrent.Callable;

class CrashReport$7 implements Callable {
   CrashReport$7(CrashReport var1) {
      super();
      this.field_83004_a = var1;
   }

   public String call() {
      byte var1 = 0;
      int var2 = 56 * var1;
      int var3 = var2 / 1024 / 1024;
      byte var4 = 0;
      int var5 = 56 * var4;
      int var6 = var5 / 1024 / 1024;
      return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; " + var6 + " MB) used";
   }
}
