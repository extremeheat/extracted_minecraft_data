package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$7 implements Callable {
   WorldInfo$7(WorldInfo var1) {
      super();
      this.field_85113_a = var1;
   }

   public String call() {
      String var1 = "Unknown?";

      try {
         switch(WorldInfo.access$900(this.field_85113_a)) {
            case 19132:
               var1 = "McRegion";
               break;
            case 19133:
               var1 = "Anvil";
         }
      } catch (Throwable var3) {
      }

      return String.format("0x%05X - %s", WorldInfo.access$900(this.field_85113_a), var1);
   }
}
