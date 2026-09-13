package net.minecraft.world;

import java.util.concurrent.Callable;

class World$4 implements Callable {
   World$4(World var1) {
      super();
      this.field_151308_a = var1;
   }

   public String call() {
      return this.field_151308_a.field_73020_y.func_73148_d();
   }
}
