package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer1 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> func_202713_a(IContextExtended<R> var1, IAreaFactory<R> var2) {
      return (var3) -> {
         IArea var4 = var2.make(this.func_202706_a(var3));
         return var1.func_201489_a_(var3, (var4x, var5) -> {
            var1.func_202698_a((long)(var4x + var3.func_202690_a()), (long)(var5 + var3.func_202691_b()));
            return this.func_202712_a(var1, var3, var4, var4x, var5);
         }, var4);
      };
   }

   int func_202712_a(IContextExtended<?> var1, AreaDimension var2, IArea var3, int var4, int var5);
}
