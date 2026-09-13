package net.minecraft.client;

import java.util.concurrent.Callable;

class Minecraft$13 implements Callable {
   Minecraft$13(Minecraft var1) {
      super();
      this.field_142056_a = var1;
   }

   public String call() {
      return this.field_142056_a.field_71424_I.field_76327_a ? this.field_142056_a.field_71424_I.func_76322_c() : "N/A (disabled)";
   }
}
