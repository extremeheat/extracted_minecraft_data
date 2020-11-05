package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class EmeraldPlacementDecorator extends SimpleFeatureDecorator<NoneDecoratorConfiguration> {
   public EmeraldPlacementDecorator(Codec<NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, NoneDecoratorConfiguration var2, BlockPos var3) {
      int var4 = 3 + var1.nextInt(6);
      return IntStream.range(0, var4).mapToObj((var2x) -> {
         int var3x = var1.nextInt(16) + var3.getX();
         int var4 = var1.nextInt(16) + var3.getZ();
         int var5 = var1.nextInt(28) + 4;
         return new BlockPos(var3x, var5, var4);
      });
   }
}
