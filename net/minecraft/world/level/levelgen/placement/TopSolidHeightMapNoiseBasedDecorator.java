package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class TopSolidHeightMapNoiseBasedDecorator extends FeatureDecorator {
   public TopSolidHeightMapNoiseBasedDecorator(Function var1) {
      super(var1);
   }

   public Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, NoiseCountFactorDecoratorConfiguration var4, BlockPos var5) {
      double var6 = Biome.BIOME_INFO_NOISE.getValue((double)var5.getX() / var4.noiseFactor, (double)var5.getZ() / var4.noiseFactor, false);
      int var8 = (int)Math.ceil((var6 + var4.noiseOffset) * (double)var4.noiseToCountRatio);
      return IntStream.range(0, var8).mapToObj((var4x) -> {
         int var5x = var3.nextInt(16) + var5.getX();
         int var6 = var3.nextInt(16) + var5.getZ();
         int var7 = var1.getHeight(var4.heightmap, var5x, var6);
         return new BlockPos(var5x, var7, var6);
      });
   }
}
