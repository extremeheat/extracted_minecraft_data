package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum GenLayerIsland implements IAreaTransformer0 {
   INSTANCE;

   private GenLayerIsland() {
   }

   public int func_202821_a(IContext var1, AreaDimension var2, int var3, int var4) {
      if (var3 == -var2.func_202690_a() && var4 == -var2.func_202691_b() && var2.func_202690_a() > -var2.func_202688_c() && var2.func_202690_a() <= 0 && var2.func_202691_b() > -var2.func_202689_d() && var2.func_202691_b() <= 0) {
         return 1;
      } else {
         return var1.func_202696_a(10) == 0 ? 1 : LayerUtil.field_202832_c;
      }
   }
}
