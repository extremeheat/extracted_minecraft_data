package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;

public interface C1Transformer extends AreaTransformer1, DimensionOffset1Transformer {
   int apply(Context var1, int var2);

   default int applyPixel(BigContext<?> var1, Area var2, int var3, int var4) {
      int var5 = var2.get(this.getParentX(var3 + 1), this.getParentY(var4 + 1));
      return this.apply(var1, var5);
   }
}
