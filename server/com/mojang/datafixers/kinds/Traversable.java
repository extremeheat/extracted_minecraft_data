package com.mojang.datafixers.kinds;

import java.util.function.Function;

public interface Traversable<T extends K1, Mu extends Traversable.Mu> extends Functor<T, Mu> {
   static <F extends K1, Mu extends Traversable.Mu> Traversable<F, Mu> unbox(App<Mu, F> var0) {
      return (Traversable)var0;
   }

   <F extends K1, A, B> App<F, App<T, B>> traverse(Applicative<F, ?> var1, Function<A, App<F, B>> var2, App<T, A> var3);

   default <F extends K1, A> App<F, App<T, A>> flip(Applicative<F, ?> var1, App<T, App<F, A>> var2) {
      return this.traverse(var1, Function.identity(), var2);
   }

   public interface Mu extends Functor.Mu {
   }
}
