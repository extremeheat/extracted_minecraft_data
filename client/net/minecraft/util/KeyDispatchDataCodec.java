package net.minecraft.util;

import com.mojang.serialization.MapCodec;

public record KeyDispatchDataCodec<A>(MapCodec<A> codec) {
   public KeyDispatchDataCodec(MapCodec<A> codec) {
      super();
      this.codec = codec;
   }

   public static <A> KeyDispatchDataCodec<A> of(MapCodec<A> var0) {
      return new KeyDispatchDataCodec(var0);
   }

   public MapCodec<A> codec() {
      return this.codec;
   }
}
