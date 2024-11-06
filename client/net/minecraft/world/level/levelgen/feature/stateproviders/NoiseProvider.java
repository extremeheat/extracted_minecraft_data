package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseProvider extends NoiseBasedStateProvider {
   public static final MapCodec<NoiseProvider> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return noiseProviderCodec(var0).apply(var0, NoiseProvider::new);
   });
   protected final List<BlockState> states;

   protected static <P extends NoiseProvider> Products.P4<RecordCodecBuilder.Mu<P>, Long, NormalNoise.NoiseParameters, Float, List<BlockState>> noiseProviderCodec(RecordCodecBuilder.Instance<P> var0) {
      return noiseCodec(var0).and(ExtraCodecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("states").forGetter((var0x) -> {
         return var0x.states;
      }));
   }

   public NoiseProvider(long var1, NormalNoise.NoiseParameters var3, float var4, List<BlockState> var5) {
      super(var1, var3, var4);
      this.states = var5;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.NOISE_PROVIDER;
   }

   public BlockState getState(RandomSource var1, BlockPos var2) {
      return this.getRandomState(this.states, var2, (double)this.scale);
   }

   protected BlockState getRandomState(List<BlockState> var1, BlockPos var2, double var3) {
      double var5 = this.getNoiseValue(var2, var3);
      return this.getRandomState(var1, var5);
   }

   protected BlockState getRandomState(List<BlockState> var1, double var2) {
      double var4 = Mth.clamp((1.0 + var2) / 2.0, 0.0, 0.9999);
      return (BlockState)var1.get((int)(var4 * (double)var1.size()));
   }
}
