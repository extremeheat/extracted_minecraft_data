package net.minecraft.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class UniformInt {
   public static final Codec<UniformInt> CODEC;
   private final int baseValue;
   private final int spread;

   public static Codec<UniformInt> codec(int var0, int var1, int var2) {
      Function var3 = (var3x) -> {
         if (var3x.baseValue >= var0 && var3x.baseValue <= var1) {
            return var3x.spread <= var2 ? DataResult.success(var3x) : DataResult.error("Spread too big: " + var3x.spread + " > " + var2);
         } else {
            return DataResult.error("Base value out of range: " + var3x.baseValue + " [" + var0 + "-" + var1 + "]");
         }
      };
      return CODEC.flatXmap(var3, var3);
   }

   private UniformInt(int var1, int var2) {
      super();
      this.baseValue = var1;
      this.spread = var2;
   }

   public static UniformInt fixed(int var0) {
      return new UniformInt(var0, 0);
   }

   public static UniformInt of(int var0, int var1) {
      return new UniformInt(var0, var1);
   }

   public int sample(Random var1) {
      return this.spread == 0 ? this.baseValue : this.baseValue + var1.nextInt(this.spread + 1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         UniformInt var2 = (UniformInt)var1;
         return this.baseValue == var2.baseValue && this.spread == var2.spread;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.baseValue, this.spread});
   }

   public String toString() {
      return "[" + this.baseValue + '-' + (this.baseValue + this.spread) + ']';
   }

   static {
      CODEC = Codec.either(Codec.INT, RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.INT.fieldOf("base").forGetter((var0x) -> {
            return var0x.baseValue;
         }), Codec.INT.fieldOf("spread").forGetter((var0x) -> {
            return var0x.spread;
         })).apply(var0, UniformInt::new);
      }).comapFlatMap((var0) -> {
         return var0.spread < 0 ? DataResult.error("Spread must be non-negative, got: " + var0.spread) : DataResult.success(var0);
      }, Function.identity())).xmap((var0) -> {
         return (UniformInt)var0.map(UniformInt::fixed, (var0x) -> {
            return var0x;
         });
      }, (var0) -> {
         return var0.spread == 0 ? Either.left(var0.baseValue) : Either.right(var0);
      });
   }
}
