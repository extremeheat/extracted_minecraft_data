package net.minecraft.world.level.newbiome.layer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C1Transformer;

public enum RareBiomeLargeLayer implements C1Transformer {
   INSTANCE;

   private static final int JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
   private static final int BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);

   private RareBiomeLargeLayer() {
   }

   public int apply(Context var1, int var2) {
      return var1.nextRandom(10) == 0 && var2 == JUNGLE ? BAMBOO_JUNGLE : var2;
   }
}
