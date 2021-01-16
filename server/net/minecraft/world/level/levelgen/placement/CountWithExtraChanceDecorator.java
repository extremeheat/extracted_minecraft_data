package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class CountWithExtraChanceDecorator extends SimpleFeatureDecorator<FrequencyWithExtraChanceDecoratorConfiguration> {
   public CountWithExtraChanceDecorator(Codec<FrequencyWithExtraChanceDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, FrequencyWithExtraChanceDecoratorConfiguration var2, BlockPos var3) {
      int var4 = var2.count + (var1.nextFloat() < var2.extraChance ? var2.extraCount : 0);
      return IntStream.range(0, var4).mapToObj((var1x) -> {
         return var3;
      });
   }
}
