package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class Spread32Decorator extends FeatureDecorator<NoneDecoratorConfiguration> {
   public Spread32Decorator(Codec<NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, NoneDecoratorConfiguration var3, BlockPos var4) {
      int var5 = var2.nextInt(var4.getY() + 32);
      return Stream.of(new BlockPos(var4.getX(), var5, var4.getZ()));
   }
}
