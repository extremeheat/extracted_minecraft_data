package com.mojang.datafixers.kinds;

import com.mojang.datafixers.util.Either;
import java.util.function.Function;

public interface CocartesianLike<T extends K1, C, Mu extends CocartesianLike.Mu> extends Functor<T, Mu>, Traversable<T, Mu> {
   static <F extends K1, C, Mu extends CocartesianLike.Mu> CocartesianLike<F, C, Mu> unbox(App<Mu, F> var0) {
      return (CocartesianLike)var0;
   }

   <A> App<Either.Mu<C>, A> to(App<T, A> var1);

   <A> App<T, A> from(App<Either.Mu<C>, A> var1);

   default <F extends K1, A, B> App<F, App<T, B>> traverse(Applicative<F, ?> var1, Function<A, App<F, B>> var2, App<T, A> var3) {
      return var1.map(this::from, (new Either.Instance()).traverse(var1, var2, this.to(var3)));
   }

   public interface Mu extends Functor.Mu, Traversable.Mu {
   }
}
