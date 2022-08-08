package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

public class ConstantInt extends IntProvider {
   public static final ConstantInt ZERO = new ConstantInt(0);
   public static final Codec<ConstantInt> CODEC;
   private final int value;

   public static ConstantInt of(int var0) {
      return var0 == 0 ? ZERO : new ConstantInt(var0);
   }

   private ConstantInt(int var1) {
      super();
      this.value = var1;
   }

   public int getValue() {
      return this.value;
   }

   public int sample(RandomSource var1) {
      return this.value;
   }

   public int getMinValue() {
      return this.value;
   }

   public int getMaxValue() {
      return this.value;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.CONSTANT;
   }

   public String toString() {
      return Integer.toString(this.value);
   }

   static {
      CODEC = Codec.either(Codec.INT, RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.INT.fieldOf("value").forGetter((var0x) -> {
            return var0x.value;
         })).apply(var0, ConstantInt::new);
      })).xmap((var0) -> {
         return (ConstantInt)var0.map(ConstantInt::of, (var0x) -> {
            return var0x;
         });
      }, (var0) -> {
         return Either.left(var0.value);
      });
   }
}
