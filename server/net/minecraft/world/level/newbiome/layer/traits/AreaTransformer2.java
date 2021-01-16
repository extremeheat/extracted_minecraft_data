package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;

public interface AreaTransformer2 extends DimensionTransformer {
   default <R extends Area> AreaFactory<R> run(BigContext<R> var1, AreaFactory<R> var2, AreaFactory<R> var3) {
      return () -> {
         Area var4 = var2.make();
         Area var5 = var3.make();
         return var1.createResult((var4x, var5x) -> {
            var1.initRandom((long)var4x, (long)var5x);
            return this.applyPixel(var1, var4, var5, var4x, var5x);
         }, var4, var5);
      };
   }

   int applyPixel(Context var1, Area var2, Area var3, int var4, int var5);
}
