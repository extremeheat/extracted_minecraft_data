package net.minecraft.world.level.levelgen.placement.nether;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.CountRangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class RandomCountRangeDecorator extends SimpleFeatureDecorator {
   public RandomCountRangeDecorator(Function var1) {
      super(var1);
   }

   public Stream place(Random var1, CountRangeDecoratorConfiguration var2, BlockPos var3) {
      int var4 = var1.nextInt(Math.max(var2.count, 1));
      return IntStream.range(0, var4).mapToObj((var3x) -> {
         int var4 = var1.nextInt(16) + var3.getX();
         int var5 = var1.nextInt(16) + var3.getZ();
         int var6 = var1.nextInt(var2.maximum - var2.topOffset) + var2.bottomOffset;
         return new BlockPos(var4, var6, var5);
      });
   }
}
