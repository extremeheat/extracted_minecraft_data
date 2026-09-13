package net.minecraft.client;

import java.util.concurrent.Callable;

class Minecraft$15 implements Callable {
   Minecraft$15(Minecraft var1) {
      super();
      this.field_152389_a = var1;
   }

   public String func_152388_a() {
      return this.field_152389_a.field_71474_y.field_151443_J == 1 ? "Off (1)" : "On (" + this.field_152389_a.field_71474_y.field_151443_J + ")";
   }
}
