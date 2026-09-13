package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$2 implements Callable {
   WorldInfo$2(WorldInfo var1) {
      super();
      this.field_85139_a = var1;
   }

   public String call() {
      return String.format(
         "ID %02d - %s, ver %d. Features enabled: %b",
         WorldInfo.access$000(this.field_85139_a).func_82747_f(),
         WorldInfo.access$000(this.field_85139_a).func_77127_a(),
         WorldInfo.access$000(this.field_85139_a).func_77131_c(),
         WorldInfo.access$100(this.field_85139_a)
      );
   }
}
