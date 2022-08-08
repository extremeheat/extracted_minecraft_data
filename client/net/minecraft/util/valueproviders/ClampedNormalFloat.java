package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class ClampedNormalFloat extends FloatProvider {
   public static final Codec<ClampedNormalFloat> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.FLOAT.fieldOf("mean").forGetter((var0x) -> {
         return var0x.mean;
      }), Codec.FLOAT.fieldOf("deviation").forGetter((var0x) -> {
         return var0x.deviation;
      }), Codec.FLOAT.fieldOf("min").forGetter((var0x) -> {
         return var0x.min;
      }), Codec.FLOAT.fieldOf("max").forGetter((var0x) -> {
         return var0x.max;
      })).apply(var0, ClampedNormalFloat::new);
   }).comapFlatMap((var0) -> {
      return var0.max < var0.min ? DataResult.error("Max must be larger than min: [" + var0.min + ", " + var0.max + "]") : DataResult.success(var0);
   }, Function.identity());
   private float mean;
   private float deviation;
   private float min;
   private float max;

   public static ClampedNormalFloat of(float var0, float var1, float var2, float var3) {
      return new ClampedNormalFloat(var0, var1, var2, var3);
   }

   private ClampedNormalFloat(float var1, float var2, float var3, float var4) {
      super();
      this.mean = var1;
      this.deviation = var2;
      this.min = var3;
      this.max = var4;
   }

   public float sample(RandomSource var1) {
      return sample(var1, this.mean, this.deviation, this.min, this.max);
   }

   public static float sample(RandomSource var0, float var1, float var2, float var3, float var4) {
      return Mth.clamp(Mth.normal(var0, var1, var2), var3, var4);
   }

   public float getMinValue() {
      return this.min;
   }

   public float getMaxValue() {
      return this.max;
   }

   public FloatProviderType<?> getType() {
      return FloatProviderType.CLAMPED_NORMAL;
   }

   public String toString() {
      return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
   }
}
