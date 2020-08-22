package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;

public class ForestRockPlacementDecorator extends FeatureDecorator {
   public ForestRockPlacementDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, FrequencyDecoratorConfiguration var4, BlockPos var5) {
      int var6 = var3.nextInt(var4.count);
      return IntStream.range(0, var6).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16) + var5.getX();
         int var5x = var3.nextInt(16) + var5.getZ();
         int var6 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var4, var5x);
         return new BlockPos(var4, var6, var5x);
      });
   }
}
