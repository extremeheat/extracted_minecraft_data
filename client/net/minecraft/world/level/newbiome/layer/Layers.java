package net.minecraft.world.level.newbiome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.LongFunction;
import net.minecraft.Util;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.LazyAreaContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public class Layers {
   private static final Int2IntMap CATEGORIES = (Int2IntMap)Util.make(new Int2IntOpenHashMap(), (var0) -> {
      register(var0, Layers.Category.BEACH, 16);
      register(var0, Layers.Category.BEACH, 26);
      register(var0, Layers.Category.DESERT, 2);
      register(var0, Layers.Category.DESERT, 17);
      register(var0, Layers.Category.DESERT, 130);
      register(var0, Layers.Category.EXTREME_HILLS, 131);
      register(var0, Layers.Category.EXTREME_HILLS, 162);
      register(var0, Layers.Category.EXTREME_HILLS, 20);
      register(var0, Layers.Category.EXTREME_HILLS, 3);
      register(var0, Layers.Category.EXTREME_HILLS, 34);
      register(var0, Layers.Category.FOREST, 27);
      register(var0, Layers.Category.FOREST, 28);
      register(var0, Layers.Category.FOREST, 29);
      register(var0, Layers.Category.FOREST, 157);
      register(var0, Layers.Category.FOREST, 132);
      register(var0, Layers.Category.FOREST, 4);
      register(var0, Layers.Category.FOREST, 155);
      register(var0, Layers.Category.FOREST, 156);
      register(var0, Layers.Category.FOREST, 18);
      register(var0, Layers.Category.ICY, 140);
      register(var0, Layers.Category.ICY, 13);
      register(var0, Layers.Category.ICY, 12);
      register(var0, Layers.Category.JUNGLE, 168);
      register(var0, Layers.Category.JUNGLE, 169);
      register(var0, Layers.Category.JUNGLE, 21);
      register(var0, Layers.Category.JUNGLE, 23);
      register(var0, Layers.Category.JUNGLE, 22);
      register(var0, Layers.Category.JUNGLE, 149);
      register(var0, Layers.Category.JUNGLE, 151);
      register(var0, Layers.Category.MESA, 37);
      register(var0, Layers.Category.MESA, 165);
      register(var0, Layers.Category.MESA, 167);
      register(var0, Layers.Category.MESA, 166);
      register(var0, Layers.Category.BADLANDS_PLATEAU, 39);
      register(var0, Layers.Category.BADLANDS_PLATEAU, 38);
      register(var0, Layers.Category.MUSHROOM, 14);
      register(var0, Layers.Category.MUSHROOM, 15);
      register(var0, Layers.Category.NONE, 25);
      register(var0, Layers.Category.OCEAN, 46);
      register(var0, Layers.Category.OCEAN, 49);
      register(var0, Layers.Category.OCEAN, 50);
      register(var0, Layers.Category.OCEAN, 48);
      register(var0, Layers.Category.OCEAN, 24);
      register(var0, Layers.Category.OCEAN, 47);
      register(var0, Layers.Category.OCEAN, 10);
      register(var0, Layers.Category.OCEAN, 45);
      register(var0, Layers.Category.OCEAN, 0);
      register(var0, Layers.Category.OCEAN, 44);
      register(var0, Layers.Category.PLAINS, 1);
      register(var0, Layers.Category.PLAINS, 129);
      register(var0, Layers.Category.RIVER, 11);
      register(var0, Layers.Category.RIVER, 7);
      register(var0, Layers.Category.SAVANNA, 35);
      register(var0, Layers.Category.SAVANNA, 36);
      register(var0, Layers.Category.SAVANNA, 163);
      register(var0, Layers.Category.SAVANNA, 164);
      register(var0, Layers.Category.SWAMP, 6);
      register(var0, Layers.Category.SWAMP, 134);
      register(var0, Layers.Category.TAIGA, 160);
      register(var0, Layers.Category.TAIGA, 161);
      register(var0, Layers.Category.TAIGA, 32);
      register(var0, Layers.Category.TAIGA, 33);
      register(var0, Layers.Category.TAIGA, 30);
      register(var0, Layers.Category.TAIGA, 31);
      register(var0, Layers.Category.TAIGA, 158);
      register(var0, Layers.Category.TAIGA, 5);
      register(var0, Layers.Category.TAIGA, 19);
      register(var0, Layers.Category.TAIGA, 133);
   });

   private static <T extends Area, C extends BigContext<T>> AreaFactory<T> zoom(long var0, AreaTransformer1 var2, AreaFactory<T> var3, int var4, LongFunction<C> var5) {
      AreaFactory var6 = var3;

      for(int var7 = 0; var7 < var4; ++var7) {
         var6 = var2.run((BigContext)var5.apply(var0 + (long)var7), var6);
      }

      return var6;
   }

   private static <T extends Area, C extends BigContext<T>> AreaFactory<T> getDefaultLayer(boolean var0, int var1, int var2, LongFunction<C> var3) {
      AreaFactory var4 = IslandLayer.INSTANCE.run((BigContext)var3.apply(1L));
      var4 = ZoomLayer.FUZZY.run((BigContext)var3.apply(2000L), var4);
      var4 = AddIslandLayer.INSTANCE.run((BigContext)var3.apply(1L), var4);
      var4 = ZoomLayer.NORMAL.run((BigContext)var3.apply(2001L), var4);
      var4 = AddIslandLayer.INSTANCE.run((BigContext)var3.apply(2L), var4);
      var4 = AddIslandLayer.INSTANCE.run((BigContext)var3.apply(50L), var4);
      var4 = AddIslandLayer.INSTANCE.run((BigContext)var3.apply(70L), var4);
      var4 = RemoveTooMuchOceanLayer.INSTANCE.run((BigContext)var3.apply(2L), var4);
      AreaFactory var5 = OceanLayer.INSTANCE.run((BigContext)var3.apply(2L));
      var5 = zoom(2001L, ZoomLayer.NORMAL, var5, 6, var3);
      var4 = AddSnowLayer.INSTANCE.run((BigContext)var3.apply(2L), var4);
      var4 = AddIslandLayer.INSTANCE.run((BigContext)var3.apply(3L), var4);
      var4 = AddEdgeLayer.CoolWarm.INSTANCE.run((BigContext)var3.apply(2L), var4);
      var4 = AddEdgeLayer.HeatIce.INSTANCE.run((BigContext)var3.apply(2L), var4);
      var4 = AddEdgeLayer.IntroduceSpecial.INSTANCE.run((BigContext)var3.apply(3L), var4);
      var4 = ZoomLayer.NORMAL.run((BigContext)var3.apply(2002L), var4);
      var4 = ZoomLayer.NORMAL.run((BigContext)var3.apply(2003L), var4);
      var4 = AddIslandLayer.INSTANCE.run((BigContext)var3.apply(4L), var4);
      var4 = AddMushroomIslandLayer.INSTANCE.run((BigContext)var3.apply(5L), var4);
      var4 = AddDeepOceanLayer.INSTANCE.run((BigContext)var3.apply(4L), var4);
      var4 = zoom(1000L, ZoomLayer.NORMAL, var4, 0, var3);
      AreaFactory var6 = zoom(1000L, ZoomLayer.NORMAL, var4, 0, var3);
      var6 = RiverInitLayer.INSTANCE.run((BigContext)var3.apply(100L), var6);
      AreaFactory var7 = (new BiomeInitLayer(var0)).run((BigContext)var3.apply(200L), var4);
      var7 = RareBiomeLargeLayer.INSTANCE.run((BigContext)var3.apply(1001L), var7);
      var7 = zoom(1000L, ZoomLayer.NORMAL, var7, 2, var3);
      var7 = BiomeEdgeLayer.INSTANCE.run((BigContext)var3.apply(1000L), var7);
      AreaFactory var8 = zoom(1000L, ZoomLayer.NORMAL, var6, 2, var3);
      var7 = RegionHillsLayer.INSTANCE.run((BigContext)var3.apply(1000L), var7, var8);
      var6 = zoom(1000L, ZoomLayer.NORMAL, var6, 2, var3);
      var6 = zoom(1000L, ZoomLayer.NORMAL, var6, var2, var3);
      var6 = RiverLayer.INSTANCE.run((BigContext)var3.apply(1L), var6);
      var6 = SmoothLayer.INSTANCE.run((BigContext)var3.apply(1000L), var6);
      var7 = RareBiomeSpotLayer.INSTANCE.run((BigContext)var3.apply(1001L), var7);

      for(int var9 = 0; var9 < var1; ++var9) {
         var7 = ZoomLayer.NORMAL.run((BigContext)var3.apply((long)(1000 + var9)), var7);
         if (var9 == 0) {
            var7 = AddIslandLayer.INSTANCE.run((BigContext)var3.apply(3L), var7);
         }

         if (var9 == 1 || var1 == 1) {
            var7 = ShoreLayer.INSTANCE.run((BigContext)var3.apply(1000L), var7);
         }
      }

      var7 = SmoothLayer.INSTANCE.run((BigContext)var3.apply(1000L), var7);
      var7 = RiverMixerLayer.INSTANCE.run((BigContext)var3.apply(100L), var7, var6);
      var7 = OceanMixerLayer.INSTANCE.run((BigContext)var3.apply(100L), var7, var5);
      return var7;
   }

   public static Layer getDefaultLayer(long var0, boolean var2, int var3, int var4) {
      boolean var5 = true;
      AreaFactory var6 = getDefaultLayer(var2, var3, var4, (var2x) -> {
         return new LazyAreaContext(25, var0, var2x);
      });
      return new Layer(var6);
   }

   public static boolean isSame(int var0, int var1) {
      if (var0 == var1) {
         return true;
      } else {
         return CATEGORIES.get(var0) == CATEGORIES.get(var1);
      }
   }

   private static void register(Int2IntOpenHashMap var0, Layers.Category var1, int var2) {
      var0.put(var2, var1.ordinal());
   }

   protected static boolean isOcean(int var0) {
      return var0 == 44 || var0 == 45 || var0 == 0 || var0 == 46 || var0 == 10 || var0 == 47 || var0 == 48 || var0 == 24 || var0 == 49 || var0 == 50;
   }

   protected static boolean isShallowOcean(int var0) {
      return var0 == 44 || var0 == 45 || var0 == 0 || var0 == 46 || var0 == 10;
   }

   static enum Category {
      NONE,
      TAIGA,
      EXTREME_HILLS,
      JUNGLE,
      MESA,
      BADLANDS_PLATEAU,
      PLAINS,
      SAVANNA,
      ICY,
      BEACH,
      FOREST,
      OCEAN,
      DESERT,
      RIVER,
      SWAMP,
      MUSHROOM;

      private Category() {
      }
   }
}
