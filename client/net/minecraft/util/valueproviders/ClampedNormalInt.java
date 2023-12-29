package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class ClampedNormalInt extends IntProvider {
   public static final Codec<ClampedNormalInt> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.FLOAT.fieldOf("mean").forGetter(var0x -> var0x.mean),
                  Codec.FLOAT.fieldOf("deviation").forGetter(var0x -> var0x.deviation),
                  Codec.INT.fieldOf("min_inclusive").forGetter(var0x -> var0x.min_inclusive),
                  Codec.INT.fieldOf("max_inclusive").forGetter(var0x -> var0x.max_inclusive)
               )
               .apply(var0, ClampedNormalInt::new)
      )
      .comapFlatMap(
         var0 -> var0.max_inclusive < var0.min_inclusive
               ? DataResult.error(() -> "Max must be larger than min: [" + var0.min_inclusive + ", " + var0.max_inclusive + "]")
               : DataResult.success(var0),
         Function.identity()
      );
   private final float mean;
   private final float deviation;
   private final int min_inclusive;
   private final int max_inclusive;

   public static ClampedNormalInt of(float var0, float var1, int var2, int var3) {
      return new ClampedNormalInt(var0, var1, var2, var3);
   }

   private ClampedNormalInt(float var1, float var2, int var3, int var4) {
      super();
      this.mean = var1;
      this.deviation = var2;
      this.min_inclusive = var3;
      this.max_inclusive = var4;
   }

   @Override
   public int sample(RandomSource var1) {
      return sample(var1, this.mean, this.deviation, (float)this.min_inclusive, (float)this.max_inclusive);
   }

   public static int sample(RandomSource var0, float var1, float var2, float var3, float var4) {
      return (int)Mth.clamp(Mth.normal(var0, var1, var2), var3, var4);
   }

   @Override
   public int getMinValue() {
      return this.min_inclusive;
   }

   @Override
   public int getMaxValue() {
      return this.max_inclusive;
   }

   @Override
   public IntProviderType<?> getType() {
      return IntProviderType.CLAMPED_NORMAL;
   }

   @Override
   public String toString() {
      return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min_inclusive + "-" + this.max_inclusive + "]";
   }
}
