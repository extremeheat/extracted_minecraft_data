package net.minecraft.world.gen.structure;

import java.util.concurrent.Callable;

class MapGenStructure$1 implements Callable {
   MapGenStructure$1(MapGenStructure var1, int var2, int var3) {
      super();
      this.field_85168_c = var1;
      this.field_85169_a = var2;
      this.field_85167_b = var3;
   }

   public String call() {
      return this.field_85168_c.func_75047_a(this.field_85169_a, this.field_85167_b) ? "True" : "False";
   }
}
