package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;

public class TopSolidHeightMapDecorator extends FeatureDecorator {
   public TopSolidHeightMapDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, NoneDecoratorConfiguration var4, BlockPos var5) {
      int var6 = var3.nextInt(16) + var5.getX();
      int var7 = var3.nextInt(16) + var5.getZ();
      int var8 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var6, var7);
      return Stream.of(new BlockPos(var6, var8, var7));
   }
}
