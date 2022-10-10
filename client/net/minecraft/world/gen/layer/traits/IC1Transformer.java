package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;

public interface IC1Transformer extends IAreaTransformer1, IDimOffset1Transformer {
   int func_202716_a(IContext var1, int var2);

   default int func_202712_a(IContextExtended<?> var1, AreaDimension var2, IArea var3, int var4, int var5) {
      int var6 = var3.func_202678_a(var4 + 1, var5 + 1);
      return this.func_202716_a(var1, var6);
   }
}
