package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

public class BiasedToBottomInt extends IntProvider {
   public static final MapCodec<BiasedToBottomInt> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.INT.fieldOf("min_inclusive").forGetter((var0x) -> {
         return var0x.minInclusive;
      }), Codec.INT.fieldOf("max_inclusive").forGetter((var0x) -> {
         return var0x.maxInclusive;
      })).apply(var0, BiasedToBottomInt::new);
   }).validate((var0) -> {
      return var0.maxInclusive < var0.minInclusive ? DataResult.error(() -> {
         return "Max must be at least min, min_inclusive: " + var0.minInclusive + ", max_inclusive: " + var0.maxInclusive;
      }) : DataResult.success(var0);
   });
   private final int minInclusive;
   private final int maxInclusive;

   private BiasedToBottomInt(int var1, int var2) {
      super();
      this.minInclusive = var1;
      this.maxInclusive = var2;
   }

   public static BiasedToBottomInt of(int var0, int var1) {
      return new BiasedToBottomInt(var0, var1);
   }

   public int sample(RandomSource var1) {
      return this.minInclusive + var1.nextInt(var1.nextInt(this.maxInclusive - this.minInclusive + 1) + 1);
   }

   public int getMinValue() {
      return this.minInclusive;
   }

   public int getMaxValue() {
      return this.maxInclusive;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.BIASED_TO_BOTTOM;
   }

   public String toString() {
      return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}
