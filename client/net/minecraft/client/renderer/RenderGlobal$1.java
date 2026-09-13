package net.minecraft.client.renderer;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;

class RenderGlobal$1 implements Callable {
   RenderGlobal$1(RenderGlobal var1, double var2, double var4, double var6) {
      super();
      this.field_85098_d = var1;
      this.field_85101_a = var2;
      this.field_85099_b = var4;
      this.field_85100_c = var6;
   }

   public String call() {
      return CrashReportCategory.func_85074_a(this.field_85101_a, this.field_85099_b, this.field_85100_c);
   }
}
