package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class UniformInt extends IntProvider {
   public static final MapCodec<UniformInt> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.INT.fieldOf("min_inclusive").forGetter((var0x) -> {
         return var0x.minInclusive;
      }), Codec.INT.fieldOf("max_inclusive").forGetter((var0x) -> {
         return var0x.maxInclusive;
      })).apply(var0, UniformInt::new);
   }).validate((var0) -> {
      return var0.maxInclusive < var0.minInclusive ? DataResult.error(() -> {
         return "Max must be at least min, min_inclusive: " + var0.minInclusive + ", max_inclusive: " + var0.maxInclusive;
      }) : DataResult.success(var0);
   });
   private final int minInclusive;
   private final int maxInclusive;

   private UniformInt(int var1, int var2) {
      super();
      this.minInclusive = var1;
      this.maxInclusive = var2;
   }

   public static UniformInt of(int var0, int var1) {
      return new UniformInt(var0, var1);
   }

   public int sample(RandomSource var1) {
      return Mth.randomBetweenInclusive(var1, this.minInclusive, this.maxInclusive);
   }

   public int getMinValue() {
      return this.minInclusive;
   }

   public int getMaxValue() {
      return this.maxInclusive;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.UNIFORM;
   }

   public String toString() {
      return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}
