package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;

public class ChorusPlantPlacementDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
   public ChorusPlantPlacementDecorator(Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, NoneDecoratorConfiguration var4, BlockPos var5) {
      int var6 = var3.nextInt(5);
      return IntStream.range(0, var6).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16);
         int var5x = var3.nextInt(16);
         int var6 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var5.offset(var4, 0, var5x)).getY();
         if (var6 > 0) {
            int var7 = var6 - 1;
            return new BlockPos(var5.getX() + var4, var7, var5.getZ() + var5x);
         } else {
            return null;
         }
      }).filter(Objects::nonNull);
   }
}
