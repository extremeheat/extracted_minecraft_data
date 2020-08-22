package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class ChancePassthroughDecorator extends SimpleFeatureDecorator {
   public ChancePassthroughDecorator(Function var1) {
      super(var1);
   }

   public Stream place(Random var1, ChanceDecoratorConfiguration var2, BlockPos var3) {
      return var1.nextFloat() < 1.0F / (float)var2.chance ? Stream.of(var3) : Stream.empty();
   }
}
