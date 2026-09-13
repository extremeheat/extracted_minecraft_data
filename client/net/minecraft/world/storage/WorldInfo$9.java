package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$9 implements Callable {
   WorldInfo$9(WorldInfo var1) {
      super();
      this.field_85109_a = var1;
   }

   public String call() {
      return String.format(
         "Game mode: %s (ID %d). Hardcore: %b. Cheats: %b",
         WorldInfo.access$1400(this.field_85109_a).func_77149_b(),
         WorldInfo.access$1400(this.field_85109_a).func_77148_a(),
         WorldInfo.access$1500(this.field_85109_a),
         WorldInfo.access$1600(this.field_85109_a)
      );
   }
}
