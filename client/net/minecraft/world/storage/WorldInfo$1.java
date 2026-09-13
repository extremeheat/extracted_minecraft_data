package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$1 implements Callable {
   WorldInfo$1(WorldInfo var1) {
      super();
      this.field_85143_a = var1;
   }

   public String call() {
      return String.valueOf(this.field_85143_a.func_76063_b());
   }
}
