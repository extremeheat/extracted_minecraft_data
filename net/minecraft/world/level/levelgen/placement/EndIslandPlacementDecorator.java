package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class EndIslandPlacementDecorator extends SimpleFeatureDecorator {
   public EndIslandPlacementDecorator(Function var1) {
      super(var1);
   }

   public Stream place(Random var1, NoneDecoratorConfiguration var2, BlockPos var3) {
      Stream var4 = Stream.empty();
      if (var1.nextInt(14) == 0) {
         var4 = Stream.concat(var4, Stream.of(var3.offset(var1.nextInt(16), 55 + var1.nextInt(16), var1.nextInt(16))));
         if (var1.nextInt(4) == 0) {
            var4 = Stream.concat(var4, Stream.of(var3.offset(var1.nextInt(16), 55 + var1.nextInt(16), var1.nextInt(16))));
         }

         return var4;
      } else {
         return Stream.empty();
      }
   }
}
