package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.biome.Biome;

public class BiomeColors {
   private static final BiomeColors.ColorResolver GRASS_COLOR_RESOLVER = Biome::getGrassColor;
   private static final BiomeColors.ColorResolver FOLIAGE_COLOR_RESOLVER = Biome::getFoliageColor;
   private static final BiomeColors.ColorResolver WATER_COLOR_RESOLVER = (var0, var1) -> {
      return var0.getWaterColor();
   };
   private static final BiomeColors.ColorResolver WATER_FOG_COLOR_RESOLVER = (var0, var1) -> {
      return var0.getWaterFogColor();
   };

   private static int getAverageColor(BlockAndBiomeGetter var0, BlockPos var1, BiomeColors.ColorResolver var2) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = Minecraft.getInstance().options.biomeBlendRadius;
      if (var6 == 0) {
         return var2.getColor(var0.getBiome(var1), var1);
      } else {
         int var7 = (var6 * 2 + 1) * (var6 * 2 + 1);
         Cursor3D var8 = new Cursor3D(var1.getX() - var6, var1.getY(), var1.getZ() - var6, var1.getX() + var6, var1.getY(), var1.getZ() + var6);

         int var10;
         for(BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(); var8.advance(); var5 += var10 & 255) {
            var9.set(var8.nextX(), var8.nextY(), var8.nextZ());
            var10 = var2.getColor(var0.getBiome(var9), var9);
            var3 += (var10 & 16711680) >> 16;
            var4 += (var10 & '\uff00') >> 8;
         }

         return (var3 / var7 & 255) << 16 | (var4 / var7 & 255) << 8 | var5 / var7 & 255;
      }
   }

   public static int getAverageGrassColor(BlockAndBiomeGetter var0, BlockPos var1) {
      return getAverageColor(var0, var1, GRASS_COLOR_RESOLVER);
   }

   public static int getAverageFoliageColor(BlockAndBiomeGetter var0, BlockPos var1) {
      return getAverageColor(var0, var1, FOLIAGE_COLOR_RESOLVER);
   }

   public static int getAverageWaterColor(BlockAndBiomeGetter var0, BlockPos var1) {
      return getAverageColor(var0, var1, WATER_COLOR_RESOLVER);
   }

   interface ColorResolver {
      int getColor(Biome var1, BlockPos var2);
   }
}
