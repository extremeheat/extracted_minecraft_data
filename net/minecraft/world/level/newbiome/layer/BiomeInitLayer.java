package net.minecraft.world.level.newbiome.layer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C0Transformer;

public class BiomeInitLayer implements C0Transformer {
   private static final int BIRCH_FOREST;
   private static final int DESERT;
   private static final int MOUNTAINS;
   private static final int FOREST;
   private static final int SNOWY_TUNDRA;
   private static final int JUNGLE;
   private static final int BADLANDS_PLATEAU;
   private static final int WOODED_BADLANDS_PLATEAU;
   private static final int MUSHROOM_FIELDS;
   private static final int PLAINS;
   private static final int GIANT_TREE_TAIGA;
   private static final int DARK_FOREST;
   private static final int SAVANNA;
   private static final int SWAMP;
   private static final int TAIGA;
   private static final int SNOWY_TAIGA;
   private static final int[] LEGACY_WARM_BIOMES;
   private static final int[] WARM_BIOMES;
   private static final int[] MEDIUM_BIOMES;
   private static final int[] COLD_BIOMES;
   private static final int[] ICE_BIOMES;
   private final int fixedBiome;
   private int[] warmBiomes;

   public BiomeInitLayer(LevelType var1, int var2) {
      this.warmBiomes = WARM_BIOMES;
      if (var1 == LevelType.NORMAL_1_1) {
         this.warmBiomes = LEGACY_WARM_BIOMES;
         this.fixedBiome = -1;
      } else {
         this.fixedBiome = var2;
      }

   }

   public int apply(Context var1, int var2) {
      if (this.fixedBiome >= 0) {
         return this.fixedBiome;
      } else {
         int var3 = (var2 & 3840) >> 8;
         var2 &= -3841;
         if (!Layers.isOcean(var2) && var2 != MUSHROOM_FIELDS) {
            switch(var2) {
            case 1:
               if (var3 > 0) {
                  return var1.nextRandom(3) == 0 ? BADLANDS_PLATEAU : WOODED_BADLANDS_PLATEAU;
               }

               return this.warmBiomes[var1.nextRandom(this.warmBiomes.length)];
            case 2:
               if (var3 > 0) {
                  return JUNGLE;
               }

               return MEDIUM_BIOMES[var1.nextRandom(MEDIUM_BIOMES.length)];
            case 3:
               if (var3 > 0) {
                  return GIANT_TREE_TAIGA;
               }

               return COLD_BIOMES[var1.nextRandom(COLD_BIOMES.length)];
            case 4:
               return ICE_BIOMES[var1.nextRandom(ICE_BIOMES.length)];
            default:
               return MUSHROOM_FIELDS;
            }
         } else {
            return var2;
         }
      }
   }

   static {
      BIRCH_FOREST = Registry.BIOME.getId(Biomes.BIRCH_FOREST);
      DESERT = Registry.BIOME.getId(Biomes.DESERT);
      MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
      FOREST = Registry.BIOME.getId(Biomes.FOREST);
      SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
      JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
      BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.BADLANDS_PLATEAU);
      WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
      MUSHROOM_FIELDS = Registry.BIOME.getId(Biomes.MUSHROOM_FIELDS);
      PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
      GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
      DARK_FOREST = Registry.BIOME.getId(Biomes.DARK_FOREST);
      SAVANNA = Registry.BIOME.getId(Biomes.SAVANNA);
      SWAMP = Registry.BIOME.getId(Biomes.SWAMP);
      TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
      SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);
      LEGACY_WARM_BIOMES = new int[]{DESERT, FOREST, MOUNTAINS, SWAMP, PLAINS, TAIGA};
      WARM_BIOMES = new int[]{DESERT, DESERT, DESERT, SAVANNA, SAVANNA, PLAINS};
      MEDIUM_BIOMES = new int[]{FOREST, DARK_FOREST, MOUNTAINS, PLAINS, BIRCH_FOREST, SWAMP};
      COLD_BIOMES = new int[]{FOREST, MOUNTAINS, TAIGA, PLAINS};
      ICE_BIOMES = new int[]{SNOWY_TUNDRA, SNOWY_TUNDRA, SNOWY_TUNDRA, SNOWY_TAIGA};
   }
}
