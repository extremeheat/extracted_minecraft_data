package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;

public class RangeDecorator extends SimpleFeatureDecorator<RangeDecoratorConfiguration> {
   public RangeDecorator(Codec<RangeDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, RangeDecoratorConfiguration var2, BlockPos var3) {
      int var4 = var3.getX();
      int var5 = var3.getZ();
      int var6 = var1.nextInt(var2.maximum - var2.topOffset) + var2.bottomOffset;
      return Stream.of(new BlockPos(var4, var6, var5));
   }
}
