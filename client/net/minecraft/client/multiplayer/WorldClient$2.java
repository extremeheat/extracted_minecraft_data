package net.minecraft.client.multiplayer;

import java.util.concurrent.Callable;

class WorldClient$2 implements Callable {
   WorldClient$2(WorldClient var1) {
      super();
      this.field_78835_a = var1;
   }

   public String call() {
      return WorldClient.access$100(this.field_78835_a).size() + " total; " + WorldClient.access$100(this.field_78835_a).toString();
   }
}
