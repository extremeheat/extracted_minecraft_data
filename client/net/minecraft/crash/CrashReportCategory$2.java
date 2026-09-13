package net.minecraft.crash;

import java.util.concurrent.Callable;

final class CrashReportCategory$2 implements Callable {
   CrashReportCategory$2(int var1) {
      super();
      this.field_85063_a = var1;
   }

   public String call() {
      if (this.field_85063_a < 0) {
         return "Unknown? (Got " + this.field_85063_a + ")";
      } else {
         String var1 = String.format("%4s", Integer.toBinaryString(this.field_85063_a)).replace(" ", "0");
         return String.format("%1$d / 0x%1$X / 0b%2$s", this.field_85063_a, var1);
      }
   }
}
