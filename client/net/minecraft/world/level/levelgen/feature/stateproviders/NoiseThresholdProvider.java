package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseThresholdProvider extends NoiseBasedStateProvider {
   public static final MapCodec<NoiseThresholdProvider> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return noiseCodec(var0).and(var0.group(Codec.floatRange(-1.0F, 1.0F).fieldOf("threshold").forGetter((var0x) -> {
         return var0x.threshold;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("high_chance").forGetter((var0x) -> {
         return var0x.highChance;
      }), BlockState.CODEC.fieldOf("default_state").forGetter((var0x) -> {
         return var0x.defaultState;
      }), Codec.list(BlockState.CODEC).fieldOf("low_states").forGetter((var0x) -> {
         return var0x.lowStates;
      }), Codec.list(BlockState.CODEC).fieldOf("high_states").forGetter((var0x) -> {
         return var0x.highStates;
      }))).apply(var0, NoiseThresholdProvider::new);
   });
   private final float threshold;
   private final float highChance;
   private final BlockState defaultState;
   private final List<BlockState> lowStates;
   private final List<BlockState> highStates;

   public NoiseThresholdProvider(long var1, NormalNoise.NoiseParameters var3, float var4, float var5, float var6, BlockState var7, List<BlockState> var8, List<BlockState> var9) {
      super(var1, var3, var4);
      this.threshold = var5;
      this.highChance = var6;
      this.defaultState = var7;
      this.lowStates = var8;
      this.highStates = var9;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.NOISE_THRESHOLD_PROVIDER;
   }

   public BlockState getState(RandomSource var1, BlockPos var2) {
      double var3 = this.getNoiseValue(var2, (double)this.scale);
      if (var3 < (double)this.threshold) {
         return (BlockState)Util.getRandom(this.lowStates, var1);
      } else {
         return var1.nextFloat() < this.highChance ? (BlockState)Util.getRandom(this.highStates, var1) : this.defaultState;
      }
   }
}
