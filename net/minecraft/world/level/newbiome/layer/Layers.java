package net.minecraft.world.level.newbiome.layer;

import java.util.function.LongFunction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.LazyAreaContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public class Layers {
   protected static final int WARM_OCEAN;
   protected static final int LUKEWARM_OCEAN;
   protected static final int OCEAN;
   protected static final int COLD_OCEAN;
   protected static final int FROZEN_OCEAN;
   protected static final int DEEP_WARM_OCEAN;
   protected static final int DEEP_LUKEWARM_OCEAN;
   protected static final int DEEP_OCEAN;
   protected static final int DEEP_COLD_OCEAN;
   protected static final int DEEP_FROZEN_OCEAN;

   private static AreaFactory zoom(long var0, AreaTransformer1 var2, AreaFactory var3, int var4, LongFunction var5) {
      AreaFactory var6 = var3;

      for(int var7 = 0; var7 < var4; ++var7) {
         var6 = var2.run((BigContext)var5.apply(var0 + (long)var7), var6);
      }

      return var6;
   }

   public static AreaFactory getDefaultLayer(LevelType var0, OverworldGeneratorSettings var1, LongFunction var2) {
      AreaFactory var3 = IslandLayer.INSTANCE.run((BigContext)var2.apply(1L));
      var3 = ZoomLayer.FUZZY.run((BigContext)var2.apply(2000L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)var2.apply(1L), var3);
      var3 = ZoomLayer.NORMAL.run((BigContext)var2.apply(2001L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)var2.apply(2L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)var2.apply(50L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)var2.apply(70L), var3);
      var3 = RemoveTooMuchOceanLayer.INSTANCE.run((BigContext)var2.apply(2L), var3);
      AreaFactory var4 = OceanLayer.INSTANCE.run((BigContext)var2.apply(2L));
      var4 = zoom(2001L, ZoomLayer.NORMAL, var4, 6, var2);
      var3 = AddSnowLayer.INSTANCE.run((BigContext)var2.apply(2L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)var2.apply(3L), var3);
      var3 = AddEdgeLayer.CoolWarm.INSTANCE.run((BigContext)var2.apply(2L), var3);
      var3 = AddEdgeLayer.HeatIce.INSTANCE.run((BigContext)var2.apply(2L), var3);
      var3 = AddEdgeLayer.IntroduceSpecial.INSTANCE.run((BigContext)var2.apply(3L), var3);
      var3 = ZoomLayer.NORMAL.run((BigContext)var2.apply(2002L), var3);
      var3 = ZoomLayer.NORMAL.run((BigContext)var2.apply(2003L), var3);
      var3 = AddIslandLayer.INSTANCE.run((BigContext)var2.apply(4L), var3);
      var3 = AddMushroomIslandLayer.INSTANCE.run((BigContext)var2.apply(5L), var3);
      var3 = AddDeepOceanLayer.INSTANCE.run((BigContext)var2.apply(4L), var3);
      var3 = zoom(1000L, ZoomLayer.NORMAL, var3, 0, var2);
      int var5 = var0 == LevelType.LARGE_BIOMES ? 6 : var1.getBiomeSize();
      int var6 = var1.getRiverSize();
      AreaFactory var7 = zoom(1000L, ZoomLayer.NORMAL, var3, 0, var2);
      var7 = RiverInitLayer.INSTANCE.run((BigContext)var2.apply(100L), var7);
      AreaFactory var8 = (new BiomeInitLayer(var0, var1.getFixedBiome())).run((BigContext)var2.apply(200L), var3);
      var8 = RareBiomeLargeLayer.INSTANCE.run((BigContext)var2.apply(1001L), var8);
      var8 = zoom(1000L, ZoomLayer.NORMAL, var8, 2, var2);
      var8 = BiomeEdgeLayer.INSTANCE.run((BigContext)var2.apply(1000L), var8);
      AreaFactory var9 = zoom(1000L, ZoomLayer.NORMAL, var7, 2, var2);
      var8 = RegionHillsLayer.INSTANCE.run((BigContext)var2.apply(1000L), var8, var9);
      var7 = zoom(1000L, ZoomLayer.NORMAL, var7, 2, var2);
      var7 = zoom(1000L, ZoomLayer.NORMAL, var7, var6, var2);
      var7 = RiverLayer.INSTANCE.run((BigContext)var2.apply(1L), var7);
      var7 = SmoothLayer.INSTANCE.run((BigContext)var2.apply(1000L), var7);
      var8 = RareBiomeSpotLayer.INSTANCE.run((BigContext)var2.apply(1001L), var8);

      for(int var10 = 0; var10 < var5; ++var10) {
         var8 = ZoomLayer.NORMAL.run((BigContext)var2.apply((long)(1000 + var10)), var8);
         if (var10 == 0) {
            var8 = AddIslandLayer.INSTANCE.run((BigContext)var2.apply(3L), var8);
         }

         if (var10 == 1 || var5 == 1) {
            var8 = ShoreLayer.INSTANCE.run((BigContext)var2.apply(1000L), var8);
         }
      }

      var8 = SmoothLayer.INSTANCE.run((BigContext)var2.apply(1000L), var8);
      var8 = RiverMixerLayer.INSTANCE.run((BigContext)var2.apply(100L), var8, var7);
      var8 = OceanMixerLayer.INSTANCE.run((BigContext)var2.apply(100L), var8, var4);
      return var8;
   }

   public static Layer getDefaultLayer(long var0, LevelType var2, OverworldGeneratorSettings var3) {
      boolean var4 = true;
      AreaFactory var5 = getDefaultLayer(var2, var3, (var2x) -> {
         return new LazyAreaContext(25, var0, var2x);
      });
      return new Layer(var5);
   }

   public static boolean isSame(int var0, int var1) {
      if (var0 == var1) {
         return true;
      } else {
         Biome var2 = (Biome)Registry.BIOME.byId(var0);
         Biome var3 = (Biome)Registry.BIOME.byId(var1);
         if (var2 != null && var3 != null) {
            if (var2 != Biomes.WOODED_BADLANDS_PLATEAU && var2 != Biomes.BADLANDS_PLATEAU) {
               if (var2.getBiomeCategory() != Biome.BiomeCategory.NONE && var3.getBiomeCategory() != Biome.BiomeCategory.NONE && var2.getBiomeCategory() == var3.getBiomeCategory()) {
                  return true;
               } else {
                  return var2 == var3;
               }
            } else {
               return var3 == Biomes.WOODED_BADLANDS_PLATEAU || var3 == Biomes.BADLANDS_PLATEAU;
            }
         } else {
            return false;
         }
      }
   }

   protected static boolean isOcean(int var0) {
      return var0 == WARM_OCEAN || var0 == LUKEWARM_OCEAN || var0 == OCEAN || var0 == COLD_OCEAN || var0 == FROZEN_OCEAN || var0 == DEEP_WARM_OCEAN || var0 == DEEP_LUKEWARM_OCEAN || var0 == DEEP_OCEAN || var0 == DEEP_COLD_OCEAN || var0 == DEEP_FROZEN_OCEAN;
   }

   protected static boolean isShallowOcean(int var0) {
      return var0 == WARM_OCEAN || var0 == LUKEWARM_OCEAN || var0 == OCEAN || var0 == COLD_OCEAN || var0 == FROZEN_OCEAN;
   }

   static {
      WARM_OCEAN = Registry.BIOME.getId(Biomes.WARM_OCEAN);
      LUKEWARM_OCEAN = Registry.BIOME.getId(Biomes.LUKEWARM_OCEAN);
      OCEAN = Registry.BIOME.getId(Biomes.OCEAN);
      COLD_OCEAN = Registry.BIOME.getId(Biomes.COLD_OCEAN);
      FROZEN_OCEAN = Registry.BIOME.getId(Biomes.FROZEN_OCEAN);
      DEEP_WARM_OCEAN = Registry.BIOME.getId(Biomes.DEEP_WARM_OCEAN);
      DEEP_LUKEWARM_OCEAN = Registry.BIOME.getId(Biomes.DEEP_LUKEWARM_OCEAN);
      DEEP_OCEAN = Registry.BIOME.getId(Biomes.DEEP_OCEAN);
      DEEP_COLD_OCEAN = Registry.BIOME.getId(Biomes.DEEP_COLD_OCEAN);
      DEEP_FROZEN_OCEAN = Registry.BIOME.getId(Biomes.DEEP_FROZEN_OCEAN);
   }
}
