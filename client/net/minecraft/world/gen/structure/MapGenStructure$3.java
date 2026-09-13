package net.minecraft.world.gen.structure;

import java.util.concurrent.Callable;

class MapGenStructure$3 implements Callable {
   MapGenStructure$3(MapGenStructure var1) {
      super();
      this.field_85161_a = var1;
   }

   public String call() {
      return this.field_85161_a.getClass().getCanonicalName();
   }
}
