package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class EndGatewayPlacementDecorator extends FeatureDecorator {
   public EndGatewayPlacementDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, NoneDecoratorConfiguration var4, BlockPos var5) {
      if (var3.nextInt(700) == 0) {
         int var6 = var3.nextInt(16) + var5.getX();
         int var7 = var3.nextInt(16) + var5.getZ();
         int var8 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var6, var7);
         if (var8 > 0) {
            int var9 = var8 + 3 + var3.nextInt(7);
            return Stream.of(new BlockPos(var6, var9, var7));
         }
      }

      return Stream.empty();
   }
}
