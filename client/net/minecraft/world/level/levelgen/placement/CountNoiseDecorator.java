package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.configurations.NoiseDependantDecoratorConfiguration;

public class CountNoiseDecorator extends FeatureDecorator<NoiseDependantDecoratorConfiguration> {
   public CountNoiseDecorator(Codec<NoiseDependantDecoratorConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, NoiseDependantDecoratorConfiguration var3, BlockPos var4) {
      double var5 = Biome.BIOME_INFO_NOISE.getValue((double)var4.getX() / 200.0D, (double)var4.getZ() / 200.0D, false);
      int var7 = var5 < var3.noiseLevel ? var3.belowNoise : var3.aboveNoise;
      return IntStream.range(0, var7).mapToObj((var1x) -> {
         return var4;
      });
   }
}
