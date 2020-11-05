package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;

public class CountDecorator extends SimpleFeatureDecorator<CountConfiguration> {
   public CountDecorator(Codec<CountConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, CountConfiguration var2, BlockPos var3) {
      return IntStream.range(0, var2.count().sample(var1)).mapToObj((var1x) -> {
         return var3;
      });
   }
}
