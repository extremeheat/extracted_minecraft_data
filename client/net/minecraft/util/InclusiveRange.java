package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;

public record InclusiveRange<T extends Comparable<T>>(T b, T c) {
   private final T minInclusive;
   private final T maxInclusive;
   public static final Codec<InclusiveRange<Integer>> INT;

   public InclusiveRange(T var1, T var2) {
      super();
      if (var1.compareTo(var2) > 0) {
         throw new IllegalArgumentException("min_inclusive must be less than or equal to max_inclusive");
      } else {
         this.minInclusive = var1;
         this.maxInclusive = var2;
      }
   }

   public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> var0) {
      return ExtraCodecs.intervalCodec(var0, "min_inclusive", "max_inclusive", InclusiveRange::create, InclusiveRange::minInclusive, InclusiveRange::maxInclusive);
   }

   public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> var0, T var1, T var2) {
      Function var3 = (var2x) -> {
         if (var2x.minInclusive().compareTo(var1) < 0) {
            return DataResult.error("Range limit too low, expected at least " + var1 + " [" + var2x.minInclusive() + "-" + var2x.maxInclusive() + "]");
         } else {
            return var2x.maxInclusive().compareTo(var2) > 0 ? DataResult.error("Range limit too high, expected at most " + var2 + " [" + var2x.minInclusive() + "-" + var2x.maxInclusive() + "]") : DataResult.success(var2x);
         }
      };
      return codec(var0).flatXmap(var3, var3);
   }

   public static <T extends Comparable<T>> DataResult<InclusiveRange<T>> create(T var0, T var1) {
      return var0.compareTo(var1) <= 0 ? DataResult.success(new InclusiveRange(var0, var1)) : DataResult.error("min_inclusive must be less than or equal to max_inclusive");
   }

   public boolean isValueInRange(T var1) {
      return var1.compareTo(this.minInclusive) >= 0 && var1.compareTo(this.maxInclusive) <= 0;
   }

   public boolean contains(InclusiveRange<T> var1) {
      return var1.minInclusive().compareTo(this.minInclusive) >= 0 && var1.maxInclusive.compareTo(this.maxInclusive) <= 0;
   }

   public String toString() {
      return "[" + this.minInclusive + ", " + this.maxInclusive + "]";
   }

   public T minInclusive() {
      return this.minInclusive;
   }

   public T maxInclusive() {
      return this.maxInclusive;
   }

   static {
      INT = codec(Codec.INT);
   }
}
