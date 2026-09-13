package net.minecraft.world.gen.structure;

import java.util.concurrent.Callable;
import net.minecraft.world.ChunkCoordIntPair;

class MapGenStructure$2 implements Callable {
   MapGenStructure$2(MapGenStructure var1, int var2, int var3) {
      super();
      this.field_85164_c = var1;
      this.field_85165_a = var2;
      this.field_85163_b = var3;
   }

   public String call() {
      return String.valueOf(ChunkCoordIntPair.func_77272_a(this.field_85165_a, this.field_85163_b));
   }
}
