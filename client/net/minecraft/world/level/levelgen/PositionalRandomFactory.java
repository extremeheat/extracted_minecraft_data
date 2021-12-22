package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public interface PositionalRandomFactory {
   // $FF: renamed from: at (net.minecraft.core.BlockPos) net.minecraft.world.level.levelgen.RandomSource
   default RandomSource method_5(BlockPos var1) {
      return this.method_6(var1.getX(), var1.getY(), var1.getZ());
   }

   default RandomSource fromHashOf(ResourceLocation var1) {
      return this.fromHashOf(var1.toString());
   }

   RandomSource fromHashOf(String var1);

   // $FF: renamed from: at (int, int, int) net.minecraft.world.level.levelgen.RandomSource
   RandomSource method_6(int var1, int var2, int var3);

   @VisibleForTesting
   void parityConfigString(StringBuilder var1);
}
