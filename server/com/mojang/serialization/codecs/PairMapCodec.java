package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Objects;
import java.util.stream.Stream;

public final class PairMapCodec<F, S> extends MapCodec<Pair<F, S>> {
   private final MapCodec<F> first;
   private final MapCodec<S> second;

   public PairMapCodec(MapCodec<F> var1, MapCodec<S> var2) {
      super();
      this.first = var1;
      this.second = var2;
   }

   public <T> DataResult<Pair<F, S>> decode(DynamicOps<T> var1, MapLike<T> var2) {
      return this.first.decode(var1, var2).flatMap((var3) -> {
         return this.second.decode(var1, var2).map((var1x) -> {
            return Pair.of(var3, var1x);
         });
      });
   }

   public <T> RecordBuilder<T> encode(Pair<F, S> var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
      return this.first.encode(var1.getFirst(), var2, this.second.encode(var1.getSecond(), var2, var3));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         PairMapCodec var2 = (PairMapCodec)var1;
         return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.first, this.second});
   }

   public String toString() {
      return "PairMapCodec[" + this.first + ", " + this.second + ']';
   }

   public <T> Stream<T> keys(DynamicOps<T> var1) {
      return Stream.concat(this.first.keys(var1), this.second.keys(var1));
   }
}
