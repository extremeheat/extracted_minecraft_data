package com.mojang.datafixers.optics;

import com.mojang.datafixers.util.Either;

public final class Inj2<F, G, G2> implements Prism<Either<F, G>, Either<F, G2>, G, G2> {
   public Inj2() {
      super();
   }

   public Either<Either<F, G2>, G> match(Either<F, G> var1) {
      return (Either)var1.map((var0) -> {
         return Either.left(Either.left(var0));
      }, Either::right);
   }

   public Either<F, G2> build(G2 var1) {
      return Either.right(var1);
   }

   public String toString() {
      return "inj2";
   }

   public boolean equals(Object var1) {
      return var1 instanceof Inj2;
   }
}
