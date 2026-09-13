package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$3 implements Callable {
   WorldInfo$3(WorldInfo var1) {
      super();
      this.field_85141_a = var1;
   }

   public String call() {
      return WorldInfo.access$200(this.field_85141_a);
   }
}
