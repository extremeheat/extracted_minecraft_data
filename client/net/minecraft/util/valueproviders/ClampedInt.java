package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class ClampedInt extends IntProvider {
   public static final MapCodec<ClampedInt> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(IntProvider.CODEC.fieldOf("source").forGetter((var0x) -> {
         return var0x.source;
      }), Codec.INT.fieldOf("min_inclusive").forGetter((var0x) -> {
         return var0x.minInclusive;
      }), Codec.INT.fieldOf("max_inclusive").forGetter((var0x) -> {
         return var0x.maxInclusive;
      })).apply(var0, ClampedInt::new);
   }).validate((var0) -> {
      return var0.maxInclusive < var0.minInclusive ? DataResult.error(() -> {
         return "Max must be at least min, min_inclusive: " + var0.minInclusive + ", max_inclusive: " + var0.maxInclusive;
      }) : DataResult.success(var0);
   });
   private final IntProvider source;
   private final int minInclusive;
   private final int maxInclusive;

   public static ClampedInt of(IntProvider var0, int var1, int var2) {
      return new ClampedInt(var0, var1, var2);
   }

   public ClampedInt(IntProvider var1, int var2, int var3) {
      super();
      this.source = var1;
      this.minInclusive = var2;
      this.maxInclusive = var3;
   }

   public int sample(RandomSource var1) {
      return Mth.clamp(this.source.sample(var1), this.minInclusive, this.maxInclusive);
   }

   public int getMinValue() {
      return Math.max(this.minInclusive, this.source.getMinValue());
   }

   public int getMaxValue() {
      return Math.min(this.maxInclusive, this.source.getMaxValue());
   }

   public IntProviderType<?> getType() {
      return IntProviderType.CLAMPED;
   }
}
