package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;

public record DimensionPadding(int bottom, int top) {
   private static final Codec<DimensionPadding> RECORD_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.NON_NEGATIVE_INT.lenientOptionalFieldOf("bottom", 0).forGetter(var0x -> var0x.bottom),
               ExtraCodecs.NON_NEGATIVE_INT.lenientOptionalFieldOf("top", 0).forGetter(var0x -> var0x.top)
            )
            .apply(var0, DimensionPadding::new)
   );
   public static final Codec<DimensionPadding> CODEC = Codec.either(ExtraCodecs.NON_NEGATIVE_INT, RECORD_CODEC)
      .xmap(
         var0 -> (DimensionPadding)var0.map(DimensionPadding::new, Function.identity()),
         var0 -> var0.hasEqualTopAndBottom() ? Either.left(var0.bottom) : Either.right(var0)
      );
   public static final DimensionPadding ZERO = new DimensionPadding(0);

   public DimensionPadding(int var1) {
      this(var1, var1);
   }

   public DimensionPadding(int bottom, int top) {
      super();
      this.bottom = bottom;
      this.top = top;
   }

   public boolean hasEqualTopAndBottom() {
      return this.top == this.bottom;
   }
}
