package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.DecoratorCountRange;

public class CountBiasedRangeDecorator extends SimpleFeatureDecorator<DecoratorCountRange> {
   public CountBiasedRangeDecorator(Function<Dynamic<?>, ? extends DecoratorCountRange> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, DecoratorCountRange var2, BlockPos var3) {
      return IntStream.range(0, var2.count).mapToObj((var3x) -> {
         int var4 = var1.nextInt(16);
         int var5 = var1.nextInt(var1.nextInt(var2.maximum - var2.topOffset) + var2.bottomOffset);
         int var6 = var1.nextInt(16);
         return var3.offset(var4, var5, var6);
      });
   }
}
