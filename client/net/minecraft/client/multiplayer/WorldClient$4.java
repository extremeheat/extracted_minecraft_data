package net.minecraft.client.multiplayer;

import java.util.concurrent.Callable;

class WorldClient$4 implements Callable {
   WorldClient$4(WorldClient var1) {
      super();
      this.field_142029_a = var1;
   }

   public String call() {
      return WorldClient.access$200(this.field_142029_a).func_71401_C() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
   }
}
