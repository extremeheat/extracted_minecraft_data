package net.minecraft.world.level.newbiome.layer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum ShoreLayer implements CastleTransformer {
   INSTANCE;

   private static final int BEACH = Registry.BIOME.getId(Biomes.BEACH);
   private static final int SNOWY_BEACH = Registry.BIOME.getId(Biomes.SNOWY_BEACH);
   private static final int DESERT = Registry.BIOME.getId(Biomes.DESERT);
   private static final int MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
   private static final int WOODED_MOUNTAINS = Registry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
   private static final int FOREST = Registry.BIOME.getId(Biomes.FOREST);
   private static final int JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
   private static final int JUNGLE_EDGE = Registry.BIOME.getId(Biomes.JUNGLE_EDGE);
   private static final int JUNGLE_HILLS = Registry.BIOME.getId(Biomes.JUNGLE_HILLS);
   private static final int BADLANDS = Registry.BIOME.getId(Biomes.BADLANDS);
   private static final int WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.BADLANDS_PLATEAU);
   private static final int ERODED_BADLANDS = Registry.BIOME.getId(Biomes.ERODED_BADLANDS);
   private static final int MODIFIED_WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU);
   private static final int MODIFIED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.MODIFIED_BADLANDS_PLATEAU);
   private static final int MUSHROOM_FIELDS = Registry.BIOME.getId(Biomes.MUSHROOM_FIELDS);
   private static final int MUSHROOM_FIELD_SHORE = Registry.BIOME.getId(Biomes.MUSHROOM_FIELD_SHORE);
   private static final int RIVER = Registry.BIOME.getId(Biomes.RIVER);
   private static final int MOUNTAIN_EDGE = Registry.BIOME.getId(Biomes.MOUNTAIN_EDGE);
   private static final int STONE_SHORE = Registry.BIOME.getId(Biomes.STONE_SHORE);
   private static final int SWAMP = Registry.BIOME.getId(Biomes.SWAMP);
   private static final int TAIGA = Registry.BIOME.getId(Biomes.TAIGA);

   private ShoreLayer() {
   }

   public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
      Biome var7 = (Biome)Registry.BIOME.byId(var6);
      if (var6 == MUSHROOM_FIELDS) {
         if (Layers.isShallowOcean(var2) || Layers.isShallowOcean(var3) || Layers.isShallowOcean(var4) || Layers.isShallowOcean(var5)) {
            return MUSHROOM_FIELD_SHORE;
         }
      } else if (var7 != null && var7.getBiomeCategory() == Biome.BiomeCategory.JUNGLE) {
         if (!isJungleCompatible(var2) || !isJungleCompatible(var3) || !isJungleCompatible(var4) || !isJungleCompatible(var5)) {
            return JUNGLE_EDGE;
         }

         if (Layers.isOcean(var2) || Layers.isOcean(var3) || Layers.isOcean(var4) || Layers.isOcean(var5)) {
            return BEACH;
         }
      } else if (var6 != MOUNTAINS && var6 != WOODED_MOUNTAINS && var6 != MOUNTAIN_EDGE) {
         if (var7 != null && var7.getPrecipitation() == Biome.Precipitation.SNOW) {
            if (!Layers.isOcean(var6) && (Layers.isOcean(var2) || Layers.isOcean(var3) || Layers.isOcean(var4) || Layers.isOcean(var5))) {
               return SNOWY_BEACH;
            }
         } else if (var6 != BADLANDS && var6 != WOODED_BADLANDS_PLATEAU) {
            if (!Layers.isOcean(var6) && var6 != RIVER && var6 != SWAMP && (Layers.isOcean(var2) || Layers.isOcean(var3) || Layers.isOcean(var4) || Layers.isOcean(var5))) {
               return BEACH;
            }
         } else if (!Layers.isOcean(var2) && !Layers.isOcean(var3) && !Layers.isOcean(var4) && !Layers.isOcean(var5) && (!this.isMesa(var2) || !this.isMesa(var3) || !this.isMesa(var4) || !this.isMesa(var5))) {
            return DESERT;
         }
      } else if (!Layers.isOcean(var6) && (Layers.isOcean(var2) || Layers.isOcean(var3) || Layers.isOcean(var4) || Layers.isOcean(var5))) {
         return STONE_SHORE;
      }

      return var6;
   }

   private static boolean isJungleCompatible(int var0) {
      if (Registry.BIOME.byId(var0) != null && ((Biome)Registry.BIOME.byId(var0)).getBiomeCategory() == Biome.BiomeCategory.JUNGLE) {
         return true;
      } else {
         return var0 == JUNGLE_EDGE || var0 == JUNGLE || var0 == JUNGLE_HILLS || var0 == FOREST || var0 == TAIGA || Layers.isOcean(var0);
      }
   }

   private boolean isMesa(int var1) {
      return var1 == BADLANDS || var1 == WOODED_BADLANDS_PLATEAU || var1 == BADLANDS_PLATEAU || var1 == ERODED_BADLANDS || var1 == MODIFIED_WOODED_BADLANDS_PLATEAU || var1 == MODIFIED_BADLANDS_PLATEAU;
   }
}
