package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class LakeWaterPlacementDecorator extends FeatureDecorator {
   public LakeWaterPlacementDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, ChanceDecoratorConfiguration var4, BlockPos var5) {
      if (var3.nextInt(var4.chance) == 0) {
         int var6 = var3.nextInt(16) + var5.getX();
         int var7 = var3.nextInt(16) + var5.getZ();
         int var8 = var3.nextInt(var2.getGenDepth());
         return Stream.of(new BlockPos(var6, var8, var7));
      } else {
         return Stream.empty();
      }
   }
}
