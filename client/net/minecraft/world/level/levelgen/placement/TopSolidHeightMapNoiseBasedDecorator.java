package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class TopSolidHeightMapNoiseBasedDecorator extends FeatureDecorator<DecoratorNoiseCountFactor> {
   public TopSolidHeightMapNoiseBasedDecorator(Function<Dynamic<?>, ? extends DecoratorNoiseCountFactor> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, DecoratorNoiseCountFactor var4, BlockPos var5) {
      double var6 = Biome.BIOME_INFO_NOISE.getValue((double)var5.getX() / var4.noiseFactor, (double)var5.getZ() / var4.noiseFactor);
      int var8 = (int)Math.ceil((var6 + var4.noiseOffset) * (double)var4.noiseToCountRatio);
      return IntStream.range(0, var8).mapToObj((var4x) -> {
         int var5x = var3.nextInt(16);
         int var6 = var3.nextInt(16);
         int var7 = var1.getHeight(var4.heightmap, var5.getX() + var5x, var5.getZ() + var6);
         return new BlockPos(var5.getX() + var5x, var7, var5.getZ() + var6);
      });
   }
}
