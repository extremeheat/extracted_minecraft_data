package net.minecraft.crash;

import java.util.concurrent.Callable;

final class CrashReportCategory$3 implements Callable {
   CrashReportCategory$3(int var1, int var2, int var3) {
      super();
      this.field_85067_a = var1;
      this.field_85065_b = var2;
      this.field_85066_c = var3;
   }

   public String call() {
      return CrashReportCategory.func_85071_a(this.field_85067_a, this.field_85065_b, this.field_85066_c);
   }
}
