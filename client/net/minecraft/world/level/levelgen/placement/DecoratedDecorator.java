package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class DecoratedDecorator extends FeatureDecorator<DecoratedDecoratorConfiguration> {
   public DecoratedDecorator(Codec<DecoratedDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, DecoratedDecoratorConfiguration var3, BlockPos var4) {
      return var3.outer().getPositions(var1, var2, var4).flatMap((var3x) -> {
         return var3.inner().getPositions(var1, var2, var3x);
      });
   }
}
