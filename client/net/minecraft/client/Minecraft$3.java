package net.minecraft.client;

import java.util.concurrent.Callable;

class Minecraft$3 implements Callable {
   Minecraft$3(Minecraft var1) {
      super();
      this.field_90055_a = var1;
   }

   public String call() {
      return this.field_90055_a.field_71462_r.getClass().getCanonicalName();
   }
}
