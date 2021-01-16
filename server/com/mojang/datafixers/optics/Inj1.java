package com.mojang.datafixers.optics;

import com.mojang.datafixers.util.Either;

public final class Inj1<F, G, F2> implements Prism<Either<F, G>, Either<F2, G>, F, F2> {
   public Inj1() {
      super();
   }

   public Either<Either<F2, G>, F> match(Either<F, G> var1) {
      return (Either)var1.map(Either::right, (var0) -> {
         return Either.left(Either.right(var0));
      });
   }

   public Either<F2, G> build(F2 var1) {
      return Either.left(var1);
   }

   public String toString() {
      return "inj1";
   }

   public boolean equals(Object var1) {
      return var1 instanceof Inj1;
   }
}
