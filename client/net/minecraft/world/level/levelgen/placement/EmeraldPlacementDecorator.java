package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;

public class EmeraldPlacementDecorator extends SimpleFeatureDecorator<NoneDecoratorConfiguration> {
   public EmeraldPlacementDecorator(Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, NoneDecoratorConfiguration var2, BlockPos var3) {
      int var4 = 3 + var1.nextInt(6);
      return IntStream.range(0, var4).mapToObj((var2x) -> {
         int var3x = var1.nextInt(16);
         int var4 = var1.nextInt(28) + 4;
         int var5 = var1.nextInt(16);
         return var3.offset(var3x, var4, var5);
      });
   }
}
