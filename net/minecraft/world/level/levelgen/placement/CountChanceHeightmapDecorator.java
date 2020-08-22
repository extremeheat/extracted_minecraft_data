package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;

public class CountChanceHeightmapDecorator extends FeatureDecorator {
   public CountChanceHeightmapDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, FrequencyChanceDecoratorConfiguration var4, BlockPos var5) {
      return IntStream.range(0, var4.count).filter((var2x) -> {
         return var3.nextFloat() < var4.chance;
      }).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16) + var5.getX();
         int var5x = var3.nextInt(16) + var5.getZ();
         int var6 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var4, var5x);
         return new BlockPos(var4, var6, var5x);
      });
   }
}
