package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public record KeyDispatchDataCodec<A>(Codec<A> a) {
   private final Codec<A> codec;

   public KeyDispatchDataCodec(Codec<A> var1) {
      super();
      this.codec = var1;
   }

   public static <A> KeyDispatchDataCodec<A> of(Codec<A> var0) {
      return new KeyDispatchDataCodec<>(var0);
   }

   public static <A> KeyDispatchDataCodec<A> of(MapCodec<A> var0) {
      return new KeyDispatchDataCodec<>(var0.codec());
   }
}
