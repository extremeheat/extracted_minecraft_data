package net.minecraft.world.level.newbiome.layer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum BiomeEdgeLayer implements CastleTransformer {
   INSTANCE;

   private static final int DESERT = Registry.BIOME.getId(Biomes.DESERT);
   private static final int MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
   private static final int WOODED_MOUNTAINS = Registry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
   private static final int SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
   private static final int JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
   private static final int BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);
   private static final int JUNGLE_EDGE = Registry.BIOME.getId(Biomes.JUNGLE_EDGE);
   private static final int BADLANDS = Registry.BIOME.getId(Biomes.BADLANDS);
   private static final int BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.BADLANDS_PLATEAU);
   private static final int WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int MOUNTAIN_EDGE = Registry.BIOME.getId(Biomes.MOUNTAIN_EDGE);
   private static final int SWAMP = Registry.BIOME.getId(Biomes.SWAMP);
   private static final int TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);

   public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
      int[] var7 = new int[1];
      if (!this.checkEdge(var7, var2, var3, var4, var5, var6, MOUNTAINS, MOUNTAIN_EDGE) && !this.checkEdgeStrict(var7, var2, var3, var4, var5, var6, WOODED_BADLANDS_PLATEAU, BADLANDS) && !this.checkEdgeStrict(var7, var2, var3, var4, var5, var6, BADLANDS_PLATEAU, BADLANDS) && !this.checkEdgeStrict(var7, var2, var3, var4, var5, var6, GIANT_TREE_TAIGA, TAIGA)) {
         if (var6 == DESERT && (var2 == SNOWY_TUNDRA || var3 == SNOWY_TUNDRA || var5 == SNOWY_TUNDRA || var4 == SNOWY_TUNDRA)) {
            return WOODED_MOUNTAINS;
         } else {
            if (var6 == SWAMP) {
               if (var2 == DESERT || var3 == DESERT || var5 == DESERT || var4 == DESERT || var2 == SNOWY_TAIGA || var3 == SNOWY_TAIGA || var5 == SNOWY_TAIGA || var4 == SNOWY_TAIGA || var2 == SNOWY_TUNDRA || var3 == SNOWY_TUNDRA || var5 == SNOWY_TUNDRA || var4 == SNOWY_TUNDRA) {
                  return PLAINS;
               }

               if (var2 == JUNGLE || var4 == JUNGLE || var3 == JUNGLE || var5 == JUNGLE || var2 == BAMBOO_JUNGLE || var4 == BAMBOO_JUNGLE || var3 == BAMBOO_JUNGLE || var5 == BAMBOO_JUNGLE) {
                  return JUNGLE_EDGE;
               }
            }

            return var6;
         }
      } else {
         return var7[0];
      }
   }

   private boolean checkEdge(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (!Layers.isSame(var6, var7)) {
         return false;
      } else {
         if (this.isValidTemperatureEdge(var2, var7) && this.isValidTemperatureEdge(var3, var7) && this.isValidTemperatureEdge(var5, var7) && this.isValidTemperatureEdge(var4, var7)) {
            var1[0] = var6;
         } else {
            var1[0] = var8;
         }

         return true;
      }
   }

   private boolean checkEdgeStrict(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (var6 != var7) {
         return false;
      } else {
         if (Layers.isSame(var2, var7) && Layers.isSame(var3, var7) && Layers.isSame(var5, var7) && Layers.isSame(var4, var7)) {
            var1[0] = var6;
         } else {
            var1[0] = var8;
         }

         return true;
      }
   }

   private boolean isValidTemperatureEdge(int var1, int var2) {
      if (Layers.isSame(var1, var2)) {
         return true;
      } else {
         Biome var3 = (Biome)Registry.BIOME.byId(var1);
         Biome var4 = (Biome)Registry.BIOME.byId(var2);
         if (var3 != null && var4 != null) {
            Biome.BiomeTempCategory var5 = var3.getTemperatureCategory();
            Biome.BiomeTempCategory var6 = var4.getTemperatureCategory();
            return var5 == var6 || var5 == Biome.BiomeTempCategory.MEDIUM || var6 == Biome.BiomeTempCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }
}
