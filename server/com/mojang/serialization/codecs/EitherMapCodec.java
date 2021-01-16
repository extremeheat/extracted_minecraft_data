package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Objects;
import java.util.stream.Stream;

public final class EitherMapCodec<F, S> extends MapCodec<Either<F, S>> {
   private final MapCodec<F> first;
   private final MapCodec<S> second;

   public EitherMapCodec(MapCodec<F> var1, MapCodec<S> var2) {
      super();
      this.first = var1;
      this.second = var2;
   }

   public <T> DataResult<Either<F, S>> decode(DynamicOps<T> var1, MapLike<T> var2) {
      DataResult var3 = this.first.decode(var1, var2).map(Either::left);
      return var3.result().isPresent() ? var3 : this.second.decode(var1, var2).map(Either::right);
   }

   public <T> RecordBuilder<T> encode(Either<F, S> var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
      return (RecordBuilder)var1.map((var3x) -> {
         return this.first.encode(var3x, var2, var3);
      }, (var3x) -> {
         return this.second.encode(var3x, var2, var3);
      });
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         EitherMapCodec var2 = (EitherMapCodec)var1;
         return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.first, this.second});
   }

   public String toString() {
      return "EitherMapCodec[" + this.first + ", " + this.second + ']';
   }

   public <T> Stream<T> keys(DynamicOps<T> var1) {
      return Stream.concat(this.first.keys(var1), this.second.keys(var1));
   }
}
