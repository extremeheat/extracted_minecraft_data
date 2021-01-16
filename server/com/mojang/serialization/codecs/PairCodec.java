package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;

public final class PairCodec<F, S> implements Codec<Pair<F, S>> {
   private final Codec<F> first;
   private final Codec<S> second;

   public PairCodec(Codec<F> var1, Codec<S> var2) {
      super();
      this.first = var1;
      this.second = var2;
   }

   public <T> DataResult<Pair<Pair<F, S>, T>> decode(DynamicOps<T> var1, T var2) {
      return this.first.decode(var1, var2).flatMap((var2x) -> {
         return this.second.decode(var1, var2x.getSecond()).map((var1x) -> {
            return Pair.of(Pair.of(var2x.getFirst(), var1x.getFirst()), var1x.getSecond());
         });
      });
   }

   public <T> DataResult<T> encode(Pair<F, S> var1, DynamicOps<T> var2, T var3) {
      return this.second.encode(var1.getSecond(), var2, var3).flatMap((var3x) -> {
         return this.first.encode(var1.getFirst(), var2, var3x);
      });
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         PairCodec var2 = (PairCodec)var1;
         return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.first, this.second});
   }

   public String toString() {
      return "PairCodec[" + this.first + ", " + this.second + ']';
   }
}
