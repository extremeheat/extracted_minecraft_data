package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class CountDepthAverageDecorator extends SimpleFeatureDecorator {
   public CountDepthAverageDecorator(Function var1) {
      super(var1);
   }

   public Stream place(Random var1, DepthAverageConfigation var2, BlockPos var3) {
      int var4 = var2.count;
      int var5 = var2.baseline;
      int var6 = var2.spread;
      return IntStream.range(0, var4).mapToObj((var4x) -> {
         int var5x = var1.nextInt(16) + var3.getX();
         int var6x = var1.nextInt(16) + var3.getZ();
         int var7 = var1.nextInt(var6) + var1.nextInt(var6) - var6 + var5;
         return new BlockPos(var5x, var7, var6x);
      });
   }
}
