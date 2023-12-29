package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class ClampedInt extends IntProvider {
   public static final Codec<ClampedInt> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  IntProvider.CODEC.fieldOf("source").forGetter(var0x -> var0x.source),
                  Codec.INT.fieldOf("min_inclusive").forGetter(var0x -> var0x.minInclusive),
                  Codec.INT.fieldOf("max_inclusive").forGetter(var0x -> var0x.maxInclusive)
               )
               .apply(var0, ClampedInt::new)
      )
      .comapFlatMap(
         var0 -> var0.maxInclusive < var0.minInclusive
               ? DataResult.error(() -> "Max must be at least min, min_inclusive: " + var0.minInclusive + ", max_inclusive: " + var0.maxInclusive)
               : DataResult.success(var0),
         Function.identity()
      );
   private final IntProvider source;
   private final int minInclusive;
   private final int maxInclusive;

   public static ClampedInt of(IntProvider var0, int var1, int var2) {
      return new ClampedInt(var0, var1, var2);
   }

   public ClampedInt(IntProvider var1, int var2, int var3) {
      super();
      this.source = var1;
      this.minInclusive = var2;
      this.maxInclusive = var3;
   }

   @Override
   public int sample(RandomSource var1) {
      return Mth.clamp(this.source.sample(var1), this.minInclusive, this.maxInclusive);
   }

   @Override
   public int getMinValue() {
      return Math.max(this.minInclusive, this.source.getMinValue());
   }

   @Override
   public int getMaxValue() {
      return Math.min(this.maxInclusive, this.source.getMaxValue());
   }

   @Override
   public IntProviderType<?> getType() {
      return IntProviderType.CLAMPED;
   }
}
