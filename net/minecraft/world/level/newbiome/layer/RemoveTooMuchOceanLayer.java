package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum RemoveTooMuchOceanLayer implements CastleTransformer {
   INSTANCE;

   public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
      return Layers.isShallowOcean(var6) && Layers.isShallowOcean(var2) && Layers.isShallowOcean(var3) && Layers.isShallowOcean(var5) && Layers.isShallowOcean(var4) && var1.nextRandom(2) == 0 ? 1 : var6;
   }
}
