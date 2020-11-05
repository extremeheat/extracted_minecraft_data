package net.minecraft.world.level.newbiome.context;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public interface BigContext<R extends Area> extends Context {
   void initRandom(long var1, long var3);

   R createResult(PixelTransformer var1);

   default R createResult(PixelTransformer var1, R var2) {
      return this.createResult(var1);
   }

   default R createResult(PixelTransformer var1, R var2, R var3) {
      return this.createResult(var1);
   }

   default int random(int var1, int var2) {
      return this.nextRandom(2) == 0 ? var1 : var2;
   }

   default int random(int var1, int var2, int var3, int var4) {
      int var5 = this.nextRandom(4);
      if (var5 == 0) {
         return var1;
      } else if (var5 == 1) {
         return var2;
      } else {
         return var5 == 2 ? var3 : var4;
      }
   }
}
