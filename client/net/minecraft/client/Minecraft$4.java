package net.minecraft.client;

import java.util.concurrent.Callable;

class Minecraft$4 implements Callable {
   Minecraft$4(Minecraft var1) {
      super();
      this.field_90053_a = var1;
   }

   public String call() {
      return this.field_90053_a.field_71462_r.getClass().getCanonicalName();
   }
}
