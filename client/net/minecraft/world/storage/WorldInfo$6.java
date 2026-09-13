package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$6 implements Callable {
   WorldInfo$6(WorldInfo var1) {
      super();
      this.field_85115_a = var1;
   }

   public String call() {
      return String.valueOf(WorldInfo.access$800(this.field_85115_a));
   }
}
