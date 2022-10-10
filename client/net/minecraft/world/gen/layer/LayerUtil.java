package net.minecraft.world.gen.layer;

import com.google.common.collect.ImmutableList;
import java.util.function.LongFunction;
import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class LayerUtil {
   protected static final int field_203632_a;
   protected static final int field_203633_b;
   protected static final int field_202832_c;
   protected static final int field_203634_d;
   protected static final int field_202831_b;
   protected static final int field_203635_f;
   protected static final int field_203636_g;
   protected static final int field_202830_a;
   protected static final int field_203637_i;
   protected static final int field_203638_j;

   private static <T extends IArea, C extends IContextExtended<T>> IAreaFactory<T> func_202829_a(long var0, IAreaTransformer1 var2, IAreaFactory<T> var3, int var4, LongFunction<C> var5) {
      IAreaFactory var6 = var3;

      for(int var7 = 0; var7 < var4; ++var7) {
         var6 = var2.func_202713_a((IContextExtended)var5.apply(var0 + (long)var7), var6);
      }

      return var6;
   }

   public static <T extends IArea, C extends IContextExtended<T>> ImmutableList<IAreaFactory<T>> func_202828_a(WorldType var0, OverworldGenSettings var1, LongFunction<C> var2) {
      IAreaFactory var3 = GenLayerIsland.INSTANCE.func_202823_a((IContextExtended)var2.apply(1L));
      var3 = GenLayerZoom.FUZZY.func_202713_a((IContextExtended)var2.apply(2000L), var3);
      var3 = GenLayerAddIsland.INSTANCE.func_202713_a((IContextExtended)var2.apply(1L), var3);
      var3 = GenLayerZoom.NORMAL.func_202713_a((IContextExtended)var2.apply(2001L), var3);
      var3 = GenLayerAddIsland.INSTANCE.func_202713_a((IContextExtended)var2.apply(2L), var3);
      var3 = GenLayerAddIsland.INSTANCE.func_202713_a((IContextExtended)var2.apply(50L), var3);
      var3 = GenLayerAddIsland.INSTANCE.func_202713_a((IContextExtended)var2.apply(70L), var3);
      var3 = GenLayerRemoveTooMuchOcean.INSTANCE.func_202713_a((IContextExtended)var2.apply(2L), var3);
      IAreaFactory var4 = OceanLayer.INSTANCE.func_202823_a((IContextExtended)var2.apply(2L));
      var4 = func_202829_a(2001L, GenLayerZoom.NORMAL, var4, 6, var2);
      var3 = GenLayerAddSnow.INSTANCE.func_202713_a((IContextExtended)var2.apply(2L), var3);
      var3 = GenLayerAddIsland.INSTANCE.func_202713_a((IContextExtended)var2.apply(3L), var3);
      var3 = GenLayerEdge.CoolWarm.INSTANCE.func_202713_a((IContextExtended)var2.apply(2L), var3);
      var3 = GenLayerEdge.HeatIce.INSTANCE.func_202713_a((IContextExtended)var2.apply(2L), var3);
      var3 = GenLayerEdge.Special.INSTANCE.func_202713_a((IContextExtended)var2.apply(3L), var3);
      var3 = GenLayerZoom.NORMAL.func_202713_a((IContextExtended)var2.apply(2002L), var3);
      var3 = GenLayerZoom.NORMAL.func_202713_a((IContextExtended)var2.apply(2003L), var3);
      var3 = GenLayerAddIsland.INSTANCE.func_202713_a((IContextExtended)var2.apply(4L), var3);
      var3 = GenLayerAddMushroomIsland.INSTANCE.func_202713_a((IContextExtended)var2.apply(5L), var3);
      var3 = GenLayerDeepOcean.INSTANCE.func_202713_a((IContextExtended)var2.apply(4L), var3);
      var3 = func_202829_a(1000L, GenLayerZoom.NORMAL, var3, 0, var2);
      int var5 = 4;
      int var6 = var5;
      if (var1 != null) {
         var5 = var1.func_202200_j();
         var6 = var1.func_202198_k();
      }

      if (var0 == WorldType.field_77135_d) {
         var5 = 6;
      }

      IAreaFactory var7 = func_202829_a(1000L, GenLayerZoom.NORMAL, var3, 0, var2);
      var7 = GenLayerRiverInit.INSTANCE.func_202713_a((IContextExtended)var2.apply(100L), var7);
      IAreaFactory var8 = (new GenLayerBiome(var0, var1)).func_202713_a((IContextExtended)var2.apply(200L), var3);
      var8 = func_202829_a(1000L, GenLayerZoom.NORMAL, var8, 2, var2);
      var8 = GenLayerBiomeEdge.INSTANCE.func_202713_a((IContextExtended)var2.apply(1000L), var8);
      IAreaFactory var9 = func_202829_a(1000L, GenLayerZoom.NORMAL, var7, 2, var2);
      var8 = GenLayerHills.INSTANCE.func_202707_a((IContextExtended)var2.apply(1000L), var8, var9);
      var7 = func_202829_a(1000L, GenLayerZoom.NORMAL, var7, 2, var2);
      var7 = func_202829_a(1000L, GenLayerZoom.NORMAL, var7, var6, var2);
      var7 = GenLayerRiver.INSTANCE.func_202713_a((IContextExtended)var2.apply(1L), var7);
      var7 = GenLayerSmooth.INSTANCE.func_202713_a((IContextExtended)var2.apply(1000L), var7);
      var8 = GenLayerRareBiome.INSTANCE.func_202713_a((IContextExtended)var2.apply(1001L), var8);

      for(int var10 = 0; var10 < var5; ++var10) {
         var8 = GenLayerZoom.NORMAL.func_202713_a((IContextExtended)var2.apply((long)(1000 + var10)), var8);
         if (var10 == 0) {
            var8 = GenLayerAddIsland.INSTANCE.func_202713_a((IContextExtended)var2.apply(3L), var8);
         }

         if (var10 == 1 || var5 == 1) {
            var8 = GenLayerShore.INSTANCE.func_202713_a((IContextExtended)var2.apply(1000L), var8);
         }
      }

      var8 = GenLayerSmooth.INSTANCE.func_202713_a((IContextExtended)var2.apply(1000L), var8);
      var8 = GenLayerRiverMix.INSTANCE.func_202707_a((IContextExtended)var2.apply(100L), var8, var7);
      var8 = GenLayerMixOceans.INSTANCE.func_202707_a((IContextExtended)var2.apply(100L), var8, var4);
      IAreaFactory var11 = GenLayerVoronoiZoom.INSTANCE.func_202713_a((IContextExtended)var2.apply(10L), var8);
      return ImmutableList.of(var8, var11, var8);
   }

   public static GenLayer[] func_202824_a(long var0, WorldType var2, OverworldGenSettings var3) {
      boolean var4 = true;
      int[] var5 = new int[1];
      ImmutableList var6 = func_202828_a(var2, var3, (var3x) -> {
         int var10002 = var5[0]++;
         return new LazyAreaLayerContext(1, var5[0], var0, var3x);
      });
      GenLayer var7 = new GenLayer((IAreaFactory)var6.get(0));
      GenLayer var8 = new GenLayer((IAreaFactory)var6.get(1));
      GenLayer var9 = new GenLayer((IAreaFactory)var6.get(2));
      return new GenLayer[]{var7, var8, var9};
   }

   public static boolean func_202826_a(int var0, int var1) {
      if (var0 == var1) {
         return true;
      } else {
         Biome var2 = (Biome)IRegistry.field_212624_m.func_148754_a(var0);
         Biome var3 = (Biome)IRegistry.field_212624_m.func_148754_a(var1);
         if (var2 != null && var3 != null) {
            if (var2 != Biomes.field_150607_aa && var2 != Biomes.field_150608_ab) {
               if (var2.func_201856_r() != Biome.Category.NONE && var3.func_201856_r() != Biome.Category.NONE && var2.func_201856_r() == var3.func_201856_r()) {
                  return true;
               } else {
                  return var2 == var3;
               }
            } else {
               return var3 == Biomes.field_150607_aa || var3 == Biomes.field_150608_ab;
            }
         } else {
            return false;
         }
      }
   }

   protected static boolean func_202827_a(int var0) {
      return var0 == field_203632_a || var0 == field_203633_b || var0 == field_202832_c || var0 == field_203634_d || var0 == field_202831_b || var0 == field_203635_f || var0 == field_203636_g || var0 == field_202830_a || var0 == field_203637_i || var0 == field_203638_j;
   }

   protected static boolean func_203631_b(int var0) {
      return var0 == field_203632_a || var0 == field_203633_b || var0 == field_202832_c || var0 == field_203634_d || var0 == field_202831_b;
   }

   static {
      field_203632_a = IRegistry.field_212624_m.func_148757_b(Biomes.field_203614_T);
      field_203633_b = IRegistry.field_212624_m.func_148757_b(Biomes.field_203615_U);
      field_202832_c = IRegistry.field_212624_m.func_148757_b(Biomes.field_76771_b);
      field_203634_d = IRegistry.field_212624_m.func_148757_b(Biomes.field_203616_V);
      field_202831_b = IRegistry.field_212624_m.func_148757_b(Biomes.field_76776_l);
      field_203635_f = IRegistry.field_212624_m.func_148757_b(Biomes.field_203617_W);
      field_203636_g = IRegistry.field_212624_m.func_148757_b(Biomes.field_203618_X);
      field_202830_a = IRegistry.field_212624_m.func_148757_b(Biomes.field_150575_M);
      field_203637_i = IRegistry.field_212624_m.func_148757_b(Biomes.field_203619_Y);
      field_203638_j = IRegistry.field_212624_m.func_148757_b(Biomes.field_203620_Z);
   }
}
