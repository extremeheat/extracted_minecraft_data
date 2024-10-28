package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

public class TrapezoidFloat extends FloatProvider {
   public static final MapCodec<TrapezoidFloat> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.FLOAT.fieldOf("min").forGetter((var0x) -> {
         return var0x.min;
      }), Codec.FLOAT.fieldOf("max").forGetter((var0x) -> {
         return var0x.max;
      }), Codec.FLOAT.fieldOf("plateau").forGetter((var0x) -> {
         return var0x.plateau;
      })).apply(var0, TrapezoidFloat::new);
   }).validate((var0) -> {
      if (var0.max < var0.min) {
         return DataResult.error(() -> {
            return "Max must be larger than min: [" + var0.min + ", " + var0.max + "]";
         });
      } else {
         return var0.plateau > var0.max - var0.min ? DataResult.error(() -> {
            return "Plateau can at most be the full span: [" + var0.min + ", " + var0.max + "]";
         }) : DataResult.success(var0);
      }
   });
   private final float min;
   private final float max;
   private final float plateau;

   public static TrapezoidFloat of(float var0, float var1, float var2) {
      return new TrapezoidFloat(var0, var1, var2);
   }

   private TrapezoidFloat(float var1, float var2, float var3) {
      super();
      this.min = var1;
      this.max = var2;
      this.plateau = var3;
   }

   public float sample(RandomSource var1) {
      float var2 = this.max - this.min;
      float var3 = (var2 - this.plateau) / 2.0F;
      float var4 = var2 - var3;
      return this.min + var1.nextFloat() * var4 + var1.nextFloat() * var3;
   }

   public float getMinValue() {
      return this.min;
   }

   public float getMaxValue() {
      return this.max;
   }

   public FloatProviderType<?> getType() {
      return FloatProviderType.TRAPEZOID;
   }

   public String toString() {
      return "trapezoid(" + this.plateau + ") in [" + this.min + "-" + this.max + "]";
   }
}
