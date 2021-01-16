package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class ChanceDecorator extends SimpleFeatureDecorator<ChanceDecoratorConfiguration> {
   public ChanceDecorator(Codec<ChanceDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, ChanceDecoratorConfiguration var2, BlockPos var3) {
      return var1.nextFloat() < 1.0F / (float)var2.chance ? Stream.of(var3) : Stream.empty();
   }
}
