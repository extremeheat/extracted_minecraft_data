package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$5 implements Callable {
   WorldInfo$5(WorldInfo var1) {
      super();
      this.field_85137_a = var1;
   }

   public String call() {
      return String.format("%d game time, %d day time", WorldInfo.access$600(this.field_85137_a), WorldInfo.access$700(this.field_85137_a));
   }
}
