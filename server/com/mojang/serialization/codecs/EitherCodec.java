package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;

public final class EitherCodec<F, S> implements Codec<Either<F, S>> {
   private final Codec<F> first;
   private final Codec<S> second;

   public EitherCodec(Codec<F> var1, Codec<S> var2) {
      super();
      this.first = var1;
      this.second = var2;
   }

   public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> var1, T var2) {
      DataResult var3 = this.first.decode(var1, var2).map((var0) -> {
         return var0.mapFirst(Either::left);
      });
      return var3.result().isPresent() ? var3 : this.second.decode(var1, var2).map((var0) -> {
         return var0.mapFirst(Either::right);
      });
   }

   public <T> DataResult<T> encode(Either<F, S> var1, DynamicOps<T> var2, T var3) {
      return (DataResult)var1.map((var3x) -> {
         return this.first.encode(var3x, var2, var3);
      }, (var3x) -> {
         return this.second.encode(var3x, var2, var3);
      });
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         EitherCodec var2 = (EitherCodec)var1;
         return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.first, this.second});
   }

   public String toString() {
      return "EitherCodec[" + this.first + ", " + this.second + ']';
   }
}
