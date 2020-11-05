package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class NopePlacementDecorator extends SimpleFeatureDecorator<NoneDecoratorConfiguration> {
   public NopePlacementDecorator(Codec<NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, NoneDecoratorConfiguration var2, BlockPos var3) {
      return Stream.of(var3);
   }
}
