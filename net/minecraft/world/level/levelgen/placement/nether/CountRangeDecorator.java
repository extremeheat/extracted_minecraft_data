package net.minecraft.world.level.levelgen.placement.nether;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.CountRangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class CountRangeDecorator extends SimpleFeatureDecorator {
   public CountRangeDecorator(Function var1) {
      super(var1);
   }

   public Stream place(Random var1, CountRangeDecoratorConfiguration var2, BlockPos var3) {
      return IntStream.range(0, var2.count).mapToObj((var3x) -> {
         int var4 = var1.nextInt(16) + var3.getX();
         int var5 = var1.nextInt(16) + var3.getZ();
         int var6 = var1.nextInt(var2.maximum - var2.topOffset) + var2.bottomOffset;
         return new BlockPos(var4, var6, var5);
      });
   }
}
