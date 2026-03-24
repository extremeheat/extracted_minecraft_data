package net.minecraft.util;

import com.mojang.serialization.MapCodec;

public record KeyDispatchDataCodec<A>(MapCodec<A> codec) {
   public KeyDispatchDataCodec {
      super();
   }

   public static <A> KeyDispatchDataCodec<A> of(final MapCodec<A> codec) {
      return new KeyDispatchDataCodec<A>(codec);
   }
}
