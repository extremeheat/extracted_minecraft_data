package net.minecraft.world.gen;

import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public interface IContextExtended<R extends IArea> extends IContext {
   void func_202698_a(long var1, long var3);

   R func_201490_a_(AreaDimension var1, IPixelTransformer var2);

   default R func_201489_a_(AreaDimension var1, IPixelTransformer var2, R var3) {
      return this.func_201490_a_(var1, var2);
   }

   default R func_201488_a_(AreaDimension var1, IPixelTransformer var2, R var3, R var4) {
      return this.func_201490_a_(var1, var2);
   }

   int func_202697_a(int... var1);
}
