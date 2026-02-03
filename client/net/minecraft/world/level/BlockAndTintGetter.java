package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.lighting.LevelLightEngine;

public interface BlockAndTintGetter extends BlockGetter {
   float getShade(Direction direction, final boolean shade);

   LevelLightEngine getLightEngine();

   int getBlockTint(final BlockPos pos, final ColorResolver color);

   default int getBrightness(final LightLayer layer, final BlockPos pos) {
      return this.getLightEngine().getLayerListener(layer).getLightValue(pos);
   }

   default int getRawBrightness(final BlockPos pos, final int darkening) {
      return this.getLightEngine().getRawBrightness(pos, darkening);
   }

   default boolean canSeeSky(final BlockPos pos) {
      return this.getBrightness(LightLayer.SKY, pos) >= 15;
   }
}
