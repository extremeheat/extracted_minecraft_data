package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
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

public class DarkOakTreePlacementDecorator extends FeatureDecorator<NoneDecoratorConfiguration> {
   public DarkOakTreePlacementDecorator(Function<Dynamic<?>, ? extends NoneDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, NoneDecoratorConfiguration var4, BlockPos var5) {
      return IntStream.range(0, 16).mapToObj((var3x) -> {
         int var4 = var3x / 4;
         int var5x = var3x % 4;
         int var6 = var4 * 4 + 1 + var3.nextInt(3);
         int var7 = var5x * 4 + 1 + var3.nextInt(3);
         return var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var5.offset(var6, 0, var7));
      });
   }
}
