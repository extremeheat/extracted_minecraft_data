package net.minecraft.client;

import java.util.concurrent.Callable;

class Minecraft$5 implements Callable {
   Minecraft$5(Minecraft var1) {
      super();
      this.field_74421_a = var1;
   }

   public String call() {
      return Minecraft.access$000(this.field_74421_a);
   }
}
