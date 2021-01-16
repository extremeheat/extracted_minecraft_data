package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class IcebergPlacementDecorator extends SimpleFeatureDecorator<NoneDecoratorConfiguration> {
   public IcebergPlacementDecorator(Codec<NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, NoneDecoratorConfiguration var2, BlockPos var3) {
      int var4 = var1.nextInt(8) + 4 + var3.getX();
      int var5 = var1.nextInt(8) + 4 + var3.getZ();
      return Stream.of(new BlockPos(var4, var3.getY(), var5));
   }
}
