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

public class CountHeight64Decorator extends FeatureDecorator<DecoratorFrequency> {
   public CountHeight64Decorator(Function<Dynamic<?>, ? extends DecoratorFrequency> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, DecoratorFrequency var4, BlockPos var5) {
      return IntStream.range(0, var4.count).mapToObj((var2x) -> {
         int var3x = var3.nextInt(16);
         boolean var4 = true;
         int var5x = var3.nextInt(16);
         return var5.offset(var3x, 64, var5x);
      });
   }
}
