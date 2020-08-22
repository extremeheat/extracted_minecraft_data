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
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class ChorusPlantPlacementDecorator extends FeatureDecorator {
   public ChorusPlantPlacementDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, NoneDecoratorConfiguration var4, BlockPos var5) {
      int var6 = var3.nextInt(5);
      return IntStream.range(0, var6).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16) + var5.getX();
         int var5x = var3.nextInt(16) + var5.getZ();
         int var6 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var4, var5x);
         if (var6 > 0) {
            int var7 = var6 - 1;
            return new BlockPos(var4, var7, var5x);
         } else {
            return null;
         }
      }).filter(Objects::nonNull);
   }
}
