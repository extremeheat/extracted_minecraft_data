package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products.P4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseProvider extends NoiseBasedStateProvider {
   public static final Codec<NoiseProvider> CODEC = RecordCodecBuilder.create((var0) -> {
      return noiseProviderCodec(var0).apply(var0, NoiseProvider::new);
   });
   protected final List<BlockState> states;

   protected static <P extends NoiseProvider> P4<Mu<P>, Long, NormalNoise.NoiseParameters, Float, List<BlockState>> noiseProviderCodec(Instance<P> var0) {
      return noiseCodec(var0).and(Codec.list(BlockState.CODEC).fieldOf("states").forGetter((var0x) -> {
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

   public BlockState getState(Random var1, BlockPos var2) {
      return this.getRandomState(this.states, var2, (double)this.scale);
   }

   protected BlockState getRandomState(List<BlockState> var1, BlockPos var2, double var3) {
      double var5 = this.getNoiseValue(var2, var3);
      return this.getRandomState(var1, var5);
   }

   protected BlockState getRandomState(List<BlockState> var1, double var2) {
      double var4 = Mth.clamp((1.0D + var2) / 2.0D, 0.0D, 0.9999D);
      return (BlockState)var1.get((int)(var4 * (double)var1.size()));
   }
}
