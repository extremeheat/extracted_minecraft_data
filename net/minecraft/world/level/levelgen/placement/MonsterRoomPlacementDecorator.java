package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class MonsterRoomPlacementDecorator extends FeatureDecorator {
   public MonsterRoomPlacementDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, ChanceDecoratorConfiguration var4, BlockPos var5) {
      int var6 = var4.chance;
      return IntStream.range(0, var6).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16) + var5.getX();
         int var5x = var3.nextInt(16) + var5.getZ();
         int var6 = var3.nextInt(var2.getGenDepth());
         return new BlockPos(var4, var6, var5x);
      });
   }
}
