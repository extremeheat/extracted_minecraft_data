package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;

public record InclusiveRange<T extends Comparable<T>>(T minInclusive, T maxInclusive) {
   public static final Codec<InclusiveRange<Integer>> INT;

   public InclusiveRange {
      super();
      if (minInclusive.compareTo(maxInclusive) > 0) {
         throw new IllegalArgumentException("min_inclusive must be less than or equal to max_inclusive");
      }
   }

   public InclusiveRange(final T value) {
      this(value, value);
   }

   public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(final Codec<T> elementCodec) {
      return ExtraCodecs.intervalCodec(elementCodec, "min_inclusive", "max_inclusive", InclusiveRange::create, InclusiveRange::minInclusive, InclusiveRange::maxInclusive);
   }

   public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(final Codec<T> elementCodec, final T minAllowedInclusive, final T maxAllowedInclusive) {
      return codec(elementCodec).validate((value) -> {
         if (value.minInclusive().compareTo(minAllowedInclusive) < 0) {
            return DataResult.error(() -> {
               String var10000 = String.valueOf(minAllowedInclusive);
               return "Range limit too low, expected at least " + var10000 + " [" + String.valueOf(value.minInclusive()) + "-" + String.valueOf(value.maxInclusive()) + "]";
            });
         } else {
            return value.maxInclusive().compareTo(maxAllowedInclusive) > 0 ? DataResult.error(() -> {
               String var10000 = String.valueOf(maxAllowedInclusive);
               return "Range limit too high, expected at most " + var10000 + " [" + String.valueOf(value.minInclusive()) + "-" + String.valueOf(value.maxInclusive()) + "]";
            }) : DataResult.success(value);
         }
      });
   }

   public static <T extends Comparable<T>> DataResult<InclusiveRange<T>> create(final T minInclusive, final T maxInclusive) {
      return minInclusive.compareTo(maxInclusive) <= 0 ? DataResult.success(new InclusiveRange(minInclusive, maxInclusive)) : DataResult.error(() -> "min_inclusive must be less than or equal to max_inclusive");
   }

   public <S extends Comparable<S>> InclusiveRange<S> map(final Function<? super T, ? extends S> mapper) {
      return new InclusiveRange<S>((Comparable)mapper.apply(this.minInclusive), (Comparable)mapper.apply(this.maxInclusive));
   }

   public boolean isValueInRange(final T value) {
      return value.compareTo(this.minInclusive) >= 0 && value.compareTo(this.maxInclusive) <= 0;
   }

   public boolean contains(final InclusiveRange<T> subRange) {
      return subRange.minInclusive().compareTo(this.minInclusive) >= 0 && subRange.maxInclusive.compareTo(this.maxInclusive) <= 0;
   }

   public String toString() {
      String var10000 = String.valueOf(this.minInclusive);
      return "[" + var10000 + ", " + String.valueOf(this.maxInclusive) + "]";
   }

   static {
      INT = codec(Codec.INT);
   }
}
