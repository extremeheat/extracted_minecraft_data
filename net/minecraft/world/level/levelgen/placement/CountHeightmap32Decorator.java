package net.minecraft.world.level.levelgen.placement;

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;

public class CountHeightmap32Decorator extends FeatureDecorator {
   public CountHeightmap32Decorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, FrequencyDecoratorConfiguration var4, BlockPos var5) {
      return IntStream.range(0, var4.count).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16) + var5.getX();
         int var5x = var3.nextInt(16) + var5.getZ();
         int var6 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var4, var5x) + 32;
         return var6 <= 0 ? null : new BlockPos(var4, var3.nextInt(var6), var5x);
      }).filter(Objects::nonNull);
   }
}
