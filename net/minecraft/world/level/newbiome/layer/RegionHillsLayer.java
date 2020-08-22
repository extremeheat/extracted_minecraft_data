package net.minecraft.world.level.newbiome.layer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset1Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum RegionHillsLayer implements AreaTransformer2, DimensionOffset1Transformer {
   INSTANCE;

   private static final Logger LOGGER = LogManager.getLogger();
   private static final int BIRCH_FOREST = Registry.BIOME.getId(Biomes.BIRCH_FOREST);
   private static final int BIRCH_FOREST_HILLS = Registry.BIOME.getId(Biomes.BIRCH_FOREST_HILLS);
   private static final int DESERT = Registry.BIOME.getId(Biomes.DESERT);
   private static final int DESERT_HILLS = Registry.BIOME.getId(Biomes.DESERT_HILLS);
   private static final int MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
   private static final int WOODED_MOUNTAINS = Registry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
   private static final int FOREST = Registry.BIOME.getId(Biomes.FOREST);
   private static final int WOODED_HILLS = Registry.BIOME.getId(Biomes.WOODED_HILLS);
   private static final int SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
   private static final int SNOWY_MOUNTAIN = Registry.BIOME.getId(Biomes.SNOWY_MOUNTAINS);
   private static final int JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
   private static final int JUNGLE_HILLS = Registry.BIOME.getId(Biomes.JUNGLE_HILLS);
   private static final int BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);
   private static final int BAMBOO_JUNGLE_HILLS = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE_HILLS);
   private static final int BADLANDS = Registry.BIOME.getId(Biomes.BADLANDS);
   private static final int WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int GIANT_TREE_TAIGA_HILLS = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA_HILLS);
   private static final int DARK_FOREST = Registry.BIOME.getId(Biomes.DARK_FOREST);
   private static final int SAVANNA = Registry.BIOME.getId(Biomes.SAVANNA);
   private static final int SAVANNA_PLATEAU = Registry.BIOME.getId(Biomes.SAVANNA_PLATEAU);
   private static final int TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);
   private static final int SNOWY_TAIGA_HILLS = Registry.BIOME.getId(Biomes.SNOWY_TAIGA_HILLS);
   private static final int TAIGA_HILLS = Registry.BIOME.getId(Biomes.TAIGA_HILLS);

   public int applyPixel(Context var1, Area var2, Area var3, int var4, int var5) {
      int var6 = var2.get(this.getParentX(var4 + 1), this.getParentY(var5 + 1));
      int var7 = var3.get(this.getParentX(var4 + 1), this.getParentY(var5 + 1));
      if (var6 > 255) {
         LOGGER.debug("old! {}", var6);
      }

      int var8 = (var7 - 2) % 29;
      Biome var10;
      if (!Layers.isShallowOcean(var6) && var7 >= 2 && var8 == 1) {
         Biome var9 = (Biome)Registry.BIOME.byId(var6);
         if (var9 == null || !var9.isMutated()) {
            var10 = Biome.getMutatedVariant(var9);
            return var10 == null ? var6 : Registry.BIOME.getId(var10);
         }
      }

      if (var1.nextRandom(3) == 0 || var8 == 0) {
         int var11 = var6;
         if (var6 == DESERT) {
            var11 = DESERT_HILLS;
         } else if (var6 == FOREST) {
            var11 = WOODED_HILLS;
         } else if (var6 == BIRCH_FOREST) {
            var11 = BIRCH_FOREST_HILLS;
         } else if (var6 == DARK_FOREST) {
            var11 = PLAINS;
         } else if (var6 == TAIGA) {
            var11 = TAIGA_HILLS;
         } else if (var6 == GIANT_TREE_TAIGA) {
            var11 = GIANT_TREE_TAIGA_HILLS;
         } else if (var6 == SNOWY_TAIGA) {
            var11 = SNOWY_TAIGA_HILLS;
         } else if (var6 == PLAINS) {
            var11 = var1.nextRandom(3) == 0 ? WOODED_HILLS : FOREST;
         } else if (var6 == SNOWY_TUNDRA) {
            var11 = SNOWY_MOUNTAIN;
         } else if (var6 == JUNGLE) {
            var11 = JUNGLE_HILLS;
         } else if (var6 == BAMBOO_JUNGLE) {
            var11 = BAMBOO_JUNGLE_HILLS;
         } else if (var6 == Layers.OCEAN) {
            var11 = Layers.DEEP_OCEAN;
         } else if (var6 == Layers.LUKEWARM_OCEAN) {
            var11 = Layers.DEEP_LUKEWARM_OCEAN;
         } else if (var6 == Layers.COLD_OCEAN) {
            var11 = Layers.DEEP_COLD_OCEAN;
         } else if (var6 == Layers.FROZEN_OCEAN) {
            var11 = Layers.DEEP_FROZEN_OCEAN;
         } else if (var6 == MOUNTAINS) {
            var11 = WOODED_MOUNTAINS;
         } else if (var6 == SAVANNA) {
            var11 = SAVANNA_PLATEAU;
         } else if (Layers.isSame(var6, WOODED_BADLANDS_PLATEAU)) {
            var11 = BADLANDS;
         } else if ((var6 == Layers.DEEP_OCEAN || var6 == Layers.DEEP_LUKEWARM_OCEAN || var6 == Layers.DEEP_COLD_OCEAN || var6 == Layers.DEEP_FROZEN_OCEAN) && var1.nextRandom(3) == 0) {
            var11 = var1.nextRandom(2) == 0 ? PLAINS : FOREST;
         }

         if (var8 == 0 && var11 != var6) {
            var10 = Biome.getMutatedVariant((Biome)Registry.BIOME.byId(var11));
            var11 = var10 == null ? var6 : Registry.BIOME.getId(var10);
         }

         if (var11 != var6) {
            int var12 = 0;
            if (Layers.isSame(var2.get(this.getParentX(var4 + 1), this.getParentY(var5 + 0)), var6)) {
               ++var12;
            }

            if (Layers.isSame(var2.get(this.getParentX(var4 + 2), this.getParentY(var5 + 1)), var6)) {
               ++var12;
            }

            if (Layers.isSame(var2.get(this.getParentX(var4 + 0), this.getParentY(var5 + 1)), var6)) {
               ++var12;
            }

            if (Layers.isSame(var2.get(this.getParentX(var4 + 1), this.getParentY(var5 + 2)), var6)) {
               ++var12;
            }

            if (var12 >= 3) {
               return var11;
            }
         }
      }

      return var6;
   }
}
