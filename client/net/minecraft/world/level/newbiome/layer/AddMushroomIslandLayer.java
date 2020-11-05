package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.BishopTransformer;

public enum AddMushroomIslandLayer implements BishopTransformer {
   INSTANCE;

   private AddMushroomIslandLayer() {
   }

   public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
      return Layers.isShallowOcean(var6) && Layers.isShallowOcean(var5) && Layers.isShallowOcean(var2) && Layers.isShallowOcean(var4) && Layers.isShallowOcean(var3) && var1.nextRandom(100) == 0 ? 14 : var6;
   }
}
