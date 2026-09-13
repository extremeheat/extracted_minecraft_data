package net.minecraft.world.storage;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;

class WorldInfo$4 implements Callable {
   WorldInfo$4(WorldInfo var1) {
      super();
      this.field_85135_a = var1;
   }

   public String call() {
      return CrashReportCategory.func_85071_a(
         WorldInfo.access$300(this.field_85135_a), WorldInfo.access$400(this.field_85135_a), WorldInfo.access$500(this.field_85135_a)
      );
   }
}
