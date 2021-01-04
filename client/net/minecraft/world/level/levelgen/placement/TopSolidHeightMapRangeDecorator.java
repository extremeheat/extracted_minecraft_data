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

public class TopSolidHeightMapRangeDecorator extends FeatureDecorator<DecoratorRange> {
   public TopSolidHeightMapRangeDecorator(Function<Dynamic<?>, ? extends DecoratorRange> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, DecoratorRange var4, BlockPos var5) {
      int var6 = var3.nextInt(var4.max - var4.min) + var4.min;
      return IntStream.range(0, var6).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16);
         int var5x = var3.nextInt(16);
         int var6 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var5.getX() + var4, var5.getZ() + var5x);
         return new BlockPos(var5.getX() + var4, var6, var5.getZ() + var5x);
      });
   }
}
