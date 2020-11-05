package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;

public interface AreaTransformer1 extends DimensionTransformer {
   default <R extends Area> AreaFactory<R> run(BigContext<R> var1, AreaFactory<R> var2) {
      return () -> {
         Area var3 = var2.make();
         return var1.createResult((var3x, var4) -> {
            var1.initRandom((long)var3x, (long)var4);
            return this.applyPixel(var1, var3, var3x, var4);
         }, var3);
      };
   }

   int applyPixel(BigContext<?> var1, Area var2, int var3, int var4);
}
