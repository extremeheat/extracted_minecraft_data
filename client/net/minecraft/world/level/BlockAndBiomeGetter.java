package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;

public interface BlockAndBiomeGetter extends BlockGetter {
   Biome getBiome(BlockPos var1);

   int getBrightness(LightLayer var1, BlockPos var2);

   default boolean canSeeSky(BlockPos var1) {
      return this.getBrightness(LightLayer.SKY, var1) >= this.getMaxLightLevel();
   }

   default int getLightColor(BlockPos var1, int var2) {
      int var3 = this.getBrightness(LightLayer.SKY, var1);
      int var4 = this.getBrightness(LightLayer.BLOCK, var1);
      if (var4 < var2) {
         var4 = var2;
      }

      return var3 << 20 | var4 << 4;
   }
}
