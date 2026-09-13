package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$8 implements Callable {
   WorldInfo$8(WorldInfo var1) {
      super();
      this.field_85111_a = var1;
   }

   public String call() {
      return String.format(
         "Rain time: %d (now: %b), thunder time: %d (now: %b)",
         WorldInfo.access$1000(this.field_85111_a),
         WorldInfo.access$1100(this.field_85111_a),
         WorldInfo.access$1200(this.field_85111_a),
         WorldInfo.access$1300(this.field_85111_a)
      );
   }
}
