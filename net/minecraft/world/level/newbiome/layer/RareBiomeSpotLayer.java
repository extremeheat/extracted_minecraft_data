package net.minecraft.world.level.newbiome.layer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C1Transformer;

public enum RareBiomeSpotLayer implements C1Transformer {
   INSTANCE;

   private static final int PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
   private static final int SUNFLOWER_PLAINS = Registry.BIOME.getId(Biomes.SUNFLOWER_PLAINS);

   public int apply(Context var1, int var2) {
      return var1.nextRandom(57) == 0 && var2 == PLAINS ? SUNFLOWER_PLAINS : var2;
   }
}
