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

public class CountTopSolidDecorator extends FeatureDecorator<DecoratorFrequency> {
   public CountTopSolidDecorator(Function<Dynamic<?>, ? extends DecoratorFrequency> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, DecoratorFrequency var4, BlockPos var5) {
      return IntStream.range(0, var4.count).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16) + var5.getX();
         int var5x = var3.nextInt(16) + var5.getZ();
         return new BlockPos(var4, var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var4, var5x), var5x);
      });
   }
}
