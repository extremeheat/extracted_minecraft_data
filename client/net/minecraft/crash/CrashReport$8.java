package net.minecraft.crash;

import java.util.concurrent.Callable;
import net.minecraft.world.gen.layer.IntCache;

class CrashReport$8 implements Callable {
   CrashReport$8(CrashReport var1) {
      super();
      this.field_85086_a = var1;
   }

   public String call() {
      return IntCache.func_85144_b();
   }
}
