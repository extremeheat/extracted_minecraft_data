package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class UniformFloat extends FloatProvider {
   public static final Codec<UniformFloat> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.FLOAT.fieldOf("min_inclusive").forGetter(var0x -> var0x.minInclusive),
                  Codec.FLOAT.fieldOf("max_exclusive").forGetter(var0x -> var0x.maxExclusive)
               )
               .apply(var0, UniformFloat::new)
      )
      .comapFlatMap(
         var0 -> var0.maxExclusive <= var0.minInclusive
               ? DataResult.error(() -> "Max must be larger than min, min_inclusive: " + var0.minInclusive + ", max_exclusive: " + var0.maxExclusive)
               : DataResult.success(var0),
         Function.identity()
      );
   private final float minInclusive;
   private final float maxExclusive;

   private UniformFloat(float var1, float var2) {
      super();
      this.minInclusive = var1;
      this.maxExclusive = var2;
   }

   public static UniformFloat of(float var0, float var1) {
      if (var1 <= var0) {
         throw new IllegalArgumentException("Max must exceed min");
      } else {
         return new UniformFloat(var0, var1);
      }
   }

   @Override
   public float sample(RandomSource var1) {
      return Mth.randomBetween(var1, this.minInclusive, this.maxExclusive);
   }

   @Override
   public float getMinValue() {
      return this.minInclusive;
   }

   @Override
   public float getMaxValue() {
      return this.maxExclusive;
   }

   @Override
   public FloatProviderType<?> getType() {
      return FloatProviderType.UNIFORM;
   }

   @Override
   public String toString() {
      return "[" + this.minInclusive + "-" + this.maxExclusive + "]";
   }
}
