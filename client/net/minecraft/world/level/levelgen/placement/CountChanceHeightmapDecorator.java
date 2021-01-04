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

public class CountChanceHeightmapDecorator extends FeatureDecorator<DecoratorFrequencyChance> {
   public CountChanceHeightmapDecorator(Function<Dynamic<?>, ? extends DecoratorFrequencyChance> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, DecoratorFrequencyChance var4, BlockPos var5) {
      return IntStream.range(0, var4.count).filter((var2x) -> {
         return var3.nextFloat() < var4.chance;
      }).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16);
         int var5x = var3.nextInt(16);
         return var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var5.offset(var4, 0, var5x));
      });
   }
}
