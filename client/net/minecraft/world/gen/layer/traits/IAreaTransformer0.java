package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer0 {
   default <R extends IArea> IAreaFactory<R> func_202823_a(IContextExtended<R> var1) {
      return (var2) -> {
         return var1.func_201490_a_(var2, (var3, var4) -> {
            var1.func_202698_a((long)(var3 + var2.func_202690_a()), (long)(var4 + var2.func_202691_b()));
            return this.func_202821_a(var1, var2, var3, var4);
         });
      };
   }

   int func_202821_a(IContext var1, AreaDimension var2, int var3, int var4);
}
