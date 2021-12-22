package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public abstract class NoiseBasedStateProvider extends BlockStateProvider {
   protected final long seed;
   protected final NormalNoise.NoiseParameters parameters;
   protected final float scale;
   protected final NormalNoise noise;

   protected static <P extends NoiseBasedStateProvider> P3<Mu<P>, Long, NormalNoise.NoiseParameters, Float> noiseCodec(Instance<P> var0) {
      return var0.group(Codec.LONG.fieldOf("seed").forGetter((var0x) -> {
         return var0x.seed;
      }), NormalNoise.NoiseParameters.DIRECT_CODEC.fieldOf("noise").forGetter((var0x) -> {
         return var0x.parameters;
      }), ExtraCodecs.POSITIVE_FLOAT.fieldOf("scale").forGetter((var0x) -> {
         return var0x.scale;
      }));
   }

   protected NoiseBasedStateProvider(long var1, NormalNoise.NoiseParameters var3, float var4) {
      super();
      this.seed = var1;
      this.parameters = var3;
      this.scale = var4;
      this.noise = NormalNoise.create(new WorldgenRandom(new LegacyRandomSource(var1)), var3);
   }

   protected double getNoiseValue(BlockPos var1, double var2) {
      return this.noise.getValue((double)var1.getX() * var2, (double)var1.getY() * var2, (double)var1.getZ() * var2);
   }
}
