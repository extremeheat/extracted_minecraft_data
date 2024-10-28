package net.minecraft.client.renderer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;

public class BiomeColors {
   public static final ColorResolver GRASS_COLOR_RESOLVER = Biome::getGrassColor;
   public static final ColorResolver FOLIAGE_COLOR_RESOLVER = (var0, var1, var3) -> {
      return var0.getFoliageColor();
   };
   public static final ColorResolver WATER_COLOR_RESOLVER = (var0, var1, var3) -> {
      return var0.getWaterColor();
   };

   public BiomeColors() {
      super();
   }

   private static int getAverageColor(BlockAndTintGetter var0, BlockPos var1, ColorResolver var2) {
      return var0.getBlockTint(var1, var2);
   }

   public static int getAverageGrassColor(BlockAndTintGetter var0, BlockPos var1) {
      return getAverageColor(var0, var1, GRASS_COLOR_RESOLVER);
   }

   public static int getAverageFoliageColor(BlockAndTintGetter var0, BlockPos var1) {
      return getAverageColor(var0, var1, FOLIAGE_COLOR_RESOLVER);
   }

   public static int getAverageWaterColor(BlockAndTintGetter var0, BlockPos var1) {
      return getAverageColor(var0, var1, WATER_COLOR_RESOLVER);
   }
}
