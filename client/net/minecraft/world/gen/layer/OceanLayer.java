package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.NoiseGeneratorImproved;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum OceanLayer implements IAreaTransformer0 {
   INSTANCE;

   private OceanLayer() {
   }

   public int func_202821_a(IContext var1, AreaDimension var2, int var3, int var4) {
      NoiseGeneratorImproved var5 = var1.func_205589_a();
      double var6 = var5.func_205562_a((double)(var3 + var2.func_202690_a()) / 8.0D, (double)(var4 + var2.func_202691_b()) / 8.0D);
      if (var6 > 0.4D) {
         return LayerUtil.field_203632_a;
      } else if (var6 > 0.2D) {
         return LayerUtil.field_203633_b;
      } else if (var6 < -0.4D) {
         return LayerUtil.field_202831_b;
      } else {
         return var6 < -0.2D ? LayerUtil.field_203634_d : LayerUtil.field_202832_c;
      }
   }
}
