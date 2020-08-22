package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;

public class ChanceHeightmapDoubleDecorator extends FeatureDecorator {
   public ChanceHeightmapDoubleDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, ChanceDecoratorConfiguration var4, BlockPos var5) {
      if (var3.nextFloat() < 1.0F / (float)var4.chance) {
         int var6 = var3.nextInt(16) + var5.getX();
         int var7 = var3.nextInt(16) + var5.getZ();
         int var8 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var6, var7) * 2;
         return var8 <= 0 ? Stream.empty() : Stream.of(new BlockPos(var6, var3.nextInt(var8), var7));
      } else {
         return Stream.empty();
      }
   }
}
