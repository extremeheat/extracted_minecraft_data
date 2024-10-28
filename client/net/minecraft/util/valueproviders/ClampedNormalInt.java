package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class ClampedNormalInt extends IntProvider {
   public static final MapCodec<ClampedNormalInt> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.FLOAT.fieldOf("mean").forGetter((var0x) -> {
         return var0x.mean;
      }), Codec.FLOAT.fieldOf("deviation").forGetter((var0x) -> {
         return var0x.deviation;
      }), Codec.INT.fieldOf("min_inclusive").forGetter((var0x) -> {
         return var0x.minInclusive;
      }), Codec.INT.fieldOf("max_inclusive").forGetter((var0x) -> {
         return var0x.maxInclusive;
      })).apply(var0, ClampedNormalInt::new);
   }).validate((var0) -> {
      return var0.maxInclusive < var0.minInclusive ? DataResult.error(() -> {
         return "Max must be larger than min: [" + var0.minInclusive + ", " + var0.maxInclusive + "]";
      }) : DataResult.success(var0);
   });
   private final float mean;
   private final float deviation;
   private final int minInclusive;
   private final int maxInclusive;

   public static ClampedNormalInt of(float var0, float var1, int var2, int var3) {
      return new ClampedNormalInt(var0, var1, var2, var3);
   }

   private ClampedNormalInt(float var1, float var2, int var3, int var4) {
      super();
      this.mean = var1;
      this.deviation = var2;
      this.minInclusive = var3;
      this.maxInclusive = var4;
   }

   public int sample(RandomSource var1) {
      return sample(var1, this.mean, this.deviation, (float)this.minInclusive, (float)this.maxInclusive);
   }

   public static int sample(RandomSource var0, float var1, float var2, float var3, float var4) {
      return (int)Mth.clamp(Mth.normal(var0, var1, var2), var3, var4);
   }

   public int getMinValue() {
      return this.minInclusive;
   }

   public int getMaxValue() {
      return this.maxInclusive;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.CLAMPED_NORMAL;
   }

   public String toString() {
      return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}
