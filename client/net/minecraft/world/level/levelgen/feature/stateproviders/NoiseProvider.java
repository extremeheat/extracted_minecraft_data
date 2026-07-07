package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseProvider extends NoiseBasedStateProvider {
   public static final MapCodec<NoiseProvider> CODEC = RecordCodecBuilder.mapCodec((i) -> noiseProviderCodec(i).apply(i, NoiseProvider::new));
   protected final List<BlockState> states;

   protected static <P extends NoiseProvider> Products.P4<RecordCodecBuilder.Mu<P>, Long, NormalNoise.NoiseParameters, Float, List<BlockState>> noiseProviderCodec(final RecordCodecBuilder.Instance<P> instance) {
      return noiseCodec(instance).and(ExtraCodecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("states").forGetter((p) -> p.states));
   }

   public NoiseProvider(final long seed, final NormalNoise.NoiseParameters parameters, final float scale, final List<BlockState> states) {
      super(seed, parameters, scale);
      this.states = states;
   }

   public MapCodec<? extends NoiseProvider> codec() {
      return CODEC;
   }

   public BlockState getState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      return this.getRandomState(this.states, pos, (double)this.scale);
   }

   protected BlockState getRandomState(final List<BlockState> states, final BlockPos pos, final double scale) {
      double noiseValue = this.getNoiseValue(pos, scale);
      return this.getRandomState(states, noiseValue);
   }

   protected BlockState getRandomState(final List<BlockState> states, final double noiseValue) {
      double placementValue = Mth.clamp((1.0 + noiseValue) / 2.0, 0.0, 0.9999);
      return (BlockState)states.get((int)(placementValue * (double)states.size()));
   }
}
