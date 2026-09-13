package net.minecraft.server.integrated;

import java.util.concurrent.Callable;

class IntegratedServer$1 implements Callable {
   IntegratedServer$1(IntegratedServer var1) {
      super();
      this.field_76974_a = var1;
   }

   public String call() {
      return "Integrated Server (map_client.txt)";
   }
}
