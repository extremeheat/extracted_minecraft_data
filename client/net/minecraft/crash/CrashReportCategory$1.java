package net.minecraft.crash;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;

final class CrashReportCategory$1 implements Callable {
   CrashReportCategory$1(int var1, Block var2) {
      super();
      this.field_85080_a = var1;
      this.field_147151_b = var2;
   }

   public String call() {
      try {
         return String.format("ID #%d (%s // %s)", this.field_85080_a, this.field_147151_b.func_149739_a(), this.field_147151_b.getClass().getCanonicalName());
      } catch (Throwable var2) {
         return "ID #" + this.field_85080_a;
      }
   }
}
