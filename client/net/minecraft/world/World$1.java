package net.minecraft.world;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;

class World$1 implements Callable {
   World$1(World var1, int var2, int var3) {
      super();
      this.field_77485_a = var1;
      this.field_151302_a = var2;
      this.field_151301_b = var3;
   }

   public String call() {
      return CrashReportCategory.func_85071_a(this.field_151302_a, 0, this.field_151301_b);
   }
}
