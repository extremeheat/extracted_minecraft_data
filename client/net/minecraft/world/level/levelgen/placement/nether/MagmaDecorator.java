package net.minecraft.world.level.levelgen.placement.nether;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class MagmaDecorator extends FeatureDecorator<DecoratorFrequency> {
   public MagmaDecorator(Function<Dynamic<?>, ? extends DecoratorFrequency> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, DecoratorFrequency var4, BlockPos var5) {
      int var6 = var1.getSeaLevel() / 2 + 1;
      return IntStream.range(0, var4.count).mapToObj((var3x) -> {
         int var4 = var3.nextInt(16);
         int var5x = var6 - 5 + var3.nextInt(10);
         int var6x = var3.nextInt(16);
         return var5.offset(var4, var5x, var6x);
      });
   }
}
