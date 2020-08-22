package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class NopePlacementDecorator extends SimpleFeatureDecorator {
   public NopePlacementDecorator(Function var1) {
      super(var1);
   }

   public Stream place(Random var1, NoneDecoratorConfiguration var2, BlockPos var3) {
      return Stream.of(var3);
   }
}
