package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class LakeLavaPlacementDecorator extends FeatureDecorator {
   public LakeLavaPlacementDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, ChanceDecoratorConfiguration var4, BlockPos var5) {
      if (var3.nextInt(var4.chance / 10) == 0) {
         int var6 = var3.nextInt(16) + var5.getX();
         int var7 = var3.nextInt(16) + var5.getZ();
         int var8 = var3.nextInt(var3.nextInt(var2.getGenDepth() - 8) + 8);
         if (var8 < var1.getSeaLevel() || var3.nextInt(var4.chance / 8) == 0) {
            return Stream.of(new BlockPos(var6, var8, var7));
         }
      }

      return Stream.empty();
   }
}
