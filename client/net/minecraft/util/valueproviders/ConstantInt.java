package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

public class ConstantInt extends IntProvider {
   public static final ConstantInt ZERO = new ConstantInt(0);
   public static final Codec<ConstantInt> CODEC = Codec.either(
         Codec.INT, RecordCodecBuilder.create(var0 -> var0.group(Codec.INT.fieldOf("value").forGetter(var0x -> var0x.value)).apply(var0, ConstantInt::new))
      )
      .xmap(var0 -> (ConstantInt)var0.map(ConstantInt::of, var0x -> var0x), var0 -> Either.left(var0.value));
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

   @Override
   public int sample(RandomSource var1) {
      return this.value;
   }

   @Override
   public int getMinValue() {
      return this.value;
   }

   @Override
   public int getMaxValue() {
      return this.value;
   }

   @Override
   public IntProviderType<?> getType() {
      return IntProviderType.CONSTANT;
   }

   @Override
   public String toString() {
      return Integer.toString(this.value);
   }
}
