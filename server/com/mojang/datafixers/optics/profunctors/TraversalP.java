package com.mojang.datafixers.optics.profunctors;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.kinds.Traversable;
import com.mojang.datafixers.optics.Wander;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;

public interface TraversalP<P extends K2, Mu extends TraversalP.Mu> extends AffineP<P, Mu> {
   static <P extends K2, Proof extends TraversalP.Mu> TraversalP<P, Proof> unbox(App<Proof, P> var0) {
      return (TraversalP)var0;
   }

   <S, T, A, B> App2<P, S, T> wander(Wander<S, T, A, B> var1, App2<P, A, B> var2);

   default <T extends K1, A, B> App2<P, App<T, A>, App<T, B>> traverse(final Traversable<T, ?> var1, App2<P, A, B> var2) {
      return this.wander(new Wander<App<T, A>, App<T, B>, A, B>() {
         public <F extends K1> FunctionType<App<T, A>, App<F, App<T, B>>> wander(Applicative<F, ?> var1x, FunctionType<A, App<F, B>> var2) {
            return (var3) -> {
               return var1.traverse(var1x, var2, var3);
            };
         }
      }, var2);
   }

   default <A, B, C> App2<P, Pair<A, C>, Pair<B, C>> first(App2<P, A, B> var1) {
      return this.dimap(this.traverse(new Pair.Instance(), var1), (var0) -> {
         return var0;
      }, Pair::unbox);
   }

   default <A, B, C> App2<P, Either<A, C>, Either<B, C>> left(App2<P, A, B> var1) {
      return this.dimap(this.traverse(new Either.Instance(), var1), (var0) -> {
         return var0;
      }, Either::unbox);
   }

   default FunctorProfunctor<Traversable.Mu, P, FunctorProfunctor.Mu<Traversable.Mu>> toFP3() {
      return new FunctorProfunctor<Traversable.Mu, P, FunctorProfunctor.Mu<Traversable.Mu>>() {
         public <A, B, F extends K1> App2<P, App<F, A>, App<F, B>> distribute(App<? extends Traversable.Mu, F> var1, App2<P, A, B> var2) {
            return TraversalP.this.traverse(Traversable.unbox(var1), var2);
         }
      };
   }

   public interface Mu extends AffineP.Mu {
      TypeToken<TraversalP.Mu> TYPE_TOKEN = new TypeToken<TraversalP.Mu>() {
      };
   }
}
