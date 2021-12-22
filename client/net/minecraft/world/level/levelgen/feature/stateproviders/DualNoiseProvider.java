package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class DualNoiseProvider extends NoiseProvider {
   public static final Codec<DualNoiseProvider> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(InclusiveRange.codec(Codec.INT, 1, 64).fieldOf("variety").forGetter((var0x) -> {
         return var0x.variety;
      }), NormalNoise.NoiseParameters.DIRECT_CODEC.fieldOf("slow_noise").forGetter((var0x) -> {
         return var0x.slowNoiseParameters;
      }), ExtraCodecs.POSITIVE_FLOAT.fieldOf("slow_scale").forGetter((var0x) -> {
         return var0x.slowScale;
      })).and(noiseProviderCodec(var0)).apply(var0, DualNoiseProvider::new);
   });
   private final InclusiveRange<Integer> variety;
   private final NormalNoise.NoiseParameters slowNoiseParameters;
   private final float slowScale;
   private final NormalNoise slowNoise;

   public DualNoiseProvider(InclusiveRange<Integer> var1, NormalNoise.NoiseParameters var2, float var3, long var4, NormalNoise.NoiseParameters var6, float var7, List<BlockState> var8) {
      super(var4, var6, var7, var8);
      this.variety = var1;
      this.slowNoiseParameters = var2;
      this.slowScale = var3;
      this.slowNoise = NormalNoise.create(new WorldgenRandom(new LegacyRandomSource(var4)), var2);
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.DUAL_NOISE_PROVIDER;
   }

   public BlockState getState(Random var1, BlockPos var2) {
      double var3 = this.getSlowNoiseValue(var2);
      int var5 = (int)Mth.clampedMap(var3, -1.0D, 1.0D, (double)(Integer)this.variety.minInclusive(), (double)((Integer)this.variety.maxInclusive() + 1));
      ArrayList var6 = Lists.newArrayListWithCapacity(var5);

      for(int var7 = 0; var7 < var5; ++var7) {
         var6.add(this.getRandomState(this.states, this.getSlowNoiseValue(var2.offset(var7 * '\ud511', 0, var7 * '\u85ba'))));
      }

      return this.getRandomState(var6, var2, (double)this.scale);
   }

   protected double getSlowNoiseValue(BlockPos var1) {
      return this.slowNoise.getValue((double)((float)var1.getX() * this.slowScale), (double)((float)var1.getY() * this.slowScale), (double)((float)var1.getZ() * this.slowScale));
   }
}
