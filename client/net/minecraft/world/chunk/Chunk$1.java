package net.minecraft.world.chunk;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;

class Chunk$1 implements Callable {
   Chunk$1(Chunk var1, int var2, int var3, int var4) {
      super();
      this.field_150821_d = var1;
      this.field_150824_a = var2;
      this.field_150822_b = var3;
      this.field_150823_c = var4;
   }

   public String call() {
      return CrashReportCategory.func_85071_a(this.field_150824_a, this.field_150822_b, this.field_150823_c);
   }
}
