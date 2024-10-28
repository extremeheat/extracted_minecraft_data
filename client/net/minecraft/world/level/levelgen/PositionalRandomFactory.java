package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public interface PositionalRandomFactory {
   default RandomSource at(BlockPos var1) {
      return this.at(var1.getX(), var1.getY(), var1.getZ());
   }

   default RandomSource fromHashOf(ResourceLocation var1) {
      return this.fromHashOf(var1.toString());
   }

   RandomSource fromHashOf(String var1);

   RandomSource fromSeed(long var1);

   RandomSource at(int var1, int var2, int var3);

   @VisibleForTesting
   void parityConfigString(StringBuilder var1);
}
