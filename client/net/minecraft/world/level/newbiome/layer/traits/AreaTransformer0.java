package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;

public interface AreaTransformer0 {
   default <R extends Area> AreaFactory<R> run(BigContext<R> var1) {
      return () -> {
         return var1.createResult((var2, var3) -> {
            var1.initRandom((long)var2, (long)var3);
            return this.applyPixel(var1, var2, var3);
         });
      };
   }

   int applyPixel(Context var1, int var2, int var3);
}
