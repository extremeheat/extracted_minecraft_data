package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public abstract class NoiseBasedStateProvider implements BlockStateProvider {
   protected final long seed;
   protected final NormalNoise parameters;
   protected final float scale;
   protected final Noise noise;

   protected static <P extends NoiseBasedStateProvider> Products.P3<RecordCodecBuilder.Mu<P>, Long, NormalNoise, Float> noiseCodec(final RecordCodecBuilder.Instance<P> instance) {
      return instance.group(Codec.LONG.fieldOf("seed").forGetter((p) -> p.seed), NormalNoise.DIRECT_CODEC.fieldOf("noise").forGetter((p) -> p.parameters), ExtraCodecs.POSITIVE_FLOAT.fieldOf("scale").forGetter((p) -> p.scale));
   }

   public abstract MapCodec<? extends NoiseBasedStateProvider> codec();

   protected NoiseBasedStateProvider(final long seed, final NormalNoise parameters, final float scale) {
      super();
      this.seed = seed;
      this.parameters = parameters;
      this.scale = scale;
      this.noise = parameters.create(new WorldgenRandom(new LegacyRandomSource(seed)));
   }

   protected float getNoiseValue(final BlockPos pos, final double scale) {
      return this.noise.get((double)pos.getX() * scale, (double)pos.getY() * scale, (double)pos.getZ() * scale);
   }
}
