package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public interface PrimitiveCodec<A> extends Codec<A> {
   <T> DataResult<A> read(DynamicOps<T> var1, T var2);

   <T> T write(DynamicOps<T> var1, A var2);

   default <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
      return this.read(var1, var2).map((var1x) -> {
         return Pair.of(var1x, var1.empty());
      });
   }

   default <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3) {
      return var2.mergeToPrimitive(var3, this.write(var2, var1));
   }
}
