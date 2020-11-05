package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class SquareDecorator extends SimpleFeatureDecorator<NoneDecoratorConfiguration> {
   public SquareDecorator(Codec<NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, NoneDecoratorConfiguration var2, BlockPos var3) {
      int var4 = var1.nextInt(16) + var3.getX();
      int var5 = var1.nextInt(16) + var3.getZ();
      int var6 = var3.getY();
      return Stream.of(new BlockPos(var4, var6, var5));
   }
}
