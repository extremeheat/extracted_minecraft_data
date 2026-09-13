package net.minecraft.client.multiplayer;

import java.util.concurrent.Callable;

class WorldClient$1 implements Callable {
   WorldClient$1(WorldClient var1) {
      super();
      this.field_78833_a = var1;
   }

   public String call() {
      return WorldClient.access$000(this.field_78833_a).size() + " total; " + WorldClient.access$000(this.field_78833_a).toString();
   }
}
