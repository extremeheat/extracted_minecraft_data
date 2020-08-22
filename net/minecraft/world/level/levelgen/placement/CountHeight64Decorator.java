package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class CountHeight64Decorator extends FeatureDecorator {
   public CountHeight64Decorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, FrequencyDecoratorConfiguration var4, BlockPos var5) {
      return IntStream.range(0, var4.count).mapToObj((var2x) -> {
         int var3x = var3.nextInt(16) + var5.getX();
         int var4 = var3.nextInt(16) + var5.getZ();
         boolean var5x = true;
         return new BlockPos(var3x, 64, var4);
      });
   }
}
