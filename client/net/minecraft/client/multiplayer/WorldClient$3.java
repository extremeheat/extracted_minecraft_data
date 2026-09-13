package net.minecraft.client.multiplayer;

import java.util.concurrent.Callable;

class WorldClient$3 implements Callable {
   WorldClient$3(WorldClient var1) {
      super();
      this.field_142027_a = var1;
   }

   public String call() {
      return WorldClient.access$200(this.field_142027_a).field_71439_g.func_142021_k();
   }
}
