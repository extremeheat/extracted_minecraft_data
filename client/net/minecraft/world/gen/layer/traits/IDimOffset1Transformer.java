package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.area.AreaDimension;

public interface IDimOffset1Transformer extends IDimTransformer {
   default AreaDimension func_202706_a(AreaDimension var1) {
      return new AreaDimension(var1.func_202690_a() - 1, var1.func_202691_b() - 1, var1.func_202688_c() + 2, var1.func_202689_d() + 2);
   }
}
