package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;

public record DimensionPadding(int bottom, int top) {
   private static final Codec<DimensionPadding> RECORD_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.NON_NEGATIVE_INT.lenientOptionalFieldOf("bottom", 0).forGetter((var0x) -> {
         return var0x.bottom;
      }), ExtraCodecs.NON_NEGATIVE_INT.lenientOptionalFieldOf("top", 0).forGetter((var0x) -> {
         return var0x.top;
      })).apply(var0, DimensionPadding::new);
   });
   public static final Codec<DimensionPadding> CODEC;
   public static final DimensionPadding ZERO;

   public DimensionPadding(int var1) {
      this(var1, var1);
   }

   public DimensionPadding(int var1, int var2) {
      super();
      this.bottom = var1;
      this.top = var2;
   }

   public boolean hasEqualTopAndBottom() {
      return this.top == this.bottom;
   }

   public int bottom() {
      return this.bottom;
   }

   public int top() {
      return this.top;
   }

   static {
      CODEC = Codec.either(ExtraCodecs.NON_NEGATIVE_INT, RECORD_CODEC).xmap((var0) -> {
         return (DimensionPadding)var0.map(DimensionPadding::new, Function.identity());
      }, (var0) -> {
         return var0.hasEqualTopAndBottom() ? Either.left(var0.bottom) : Either.right(var0);
      });
      ZERO = new DimensionPadding(0);
   }
}
