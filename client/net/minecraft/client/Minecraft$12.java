package net.minecraft.client;

import java.util.concurrent.Callable;

class Minecraft$12 implements Callable {
   Minecraft$12(Minecraft var1) {
      super();
      this.field_90048_a = var1;
   }

   public String call() {
      return Minecraft.access$100(this.field_90048_a).func_135041_c().toString();
   }
}
