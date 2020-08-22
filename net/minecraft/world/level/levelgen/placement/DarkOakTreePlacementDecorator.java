package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class DarkOakTreePlacementDecorator extends FeatureDecorator {
   public DarkOakTreePlacementDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, NoneDecoratorConfiguration var4, BlockPos var5) {
      return IntStream.range(0, 16).mapToObj((var3x) -> {
         int var4 = var3x / 4;
         int var5x = var3x % 4;
         int var6 = var4 * 4 + 1 + var3.nextInt(3) + var5.getX();
         int var7 = var5x * 4 + 1 + var3.nextInt(3) + var5.getZ();
         int var8 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var6, var7);
         return new BlockPos(var6, var8, var7);
      });
   }
}
