package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

public class TrapezoidFloat extends FloatProvider {
   public static final MapCodec<TrapezoidFloat> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.fieldOf("min").forGetter((t) -> t.min), Codec.FLOAT.fieldOf("max").forGetter((t) -> t.max), Codec.FLOAT.fieldOf("plateau").forGetter((t) -> t.plateau)).apply(i, TrapezoidFloat::new)).validate((c) -> {
      if (c.max < c.min) {
         return DataResult.error(() -> "Max must be larger than min: [" + c.min + ", " + c.max + "]");
      } else {
         return c.plateau > c.max - c.min ? DataResult.error(() -> "Plateau can at most be the full span: [" + c.min + ", " + c.max + "]") : DataResult.success(c);
      }
   });
   private final float min;
   private final float max;
   private final float plateau;

   public static TrapezoidFloat of(final float min, final float max, final float plateau) {
      return new TrapezoidFloat(min, max, plateau);
   }

   private TrapezoidFloat(final float min, final float max, final float plateau) {
      super();
      this.min = min;
      this.max = max;
      this.plateau = plateau;
   }

   public float sample(final RandomSource random) {
      float range = this.max - this.min;
      float plateauStart = (range - this.plateau) / 2.0F;
      float plateauEnd = range - plateauStart;
      return this.min + random.nextFloat() * plateauEnd + random.nextFloat() * plateauStart;
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
