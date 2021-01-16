package com.mojang.datafixers.optics.profunctors;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.CocartesianLike;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.util.Either;
import java.util.function.Function;

public interface Cocartesian<P extends K2, Mu extends Cocartesian.Mu> extends Profunctor<P, Mu> {
   static <P extends K2, Proof extends Cocartesian.Mu> Cocartesian<P, Proof> unbox(App<Proof, P> var0) {
      return (Cocartesian)var0;
   }

   <A, B, C> App2<P, Either<A, C>, Either<B, C>> left(App2<P, A, B> var1);

   default <A, B, C> App2<P, Either<C, A>, Either<C, B>> right(App2<P, A, B> var1) {
      return this.dimap(this.left(var1), Either::swap, Either::swap);
   }

   default FunctorProfunctor<CocartesianLike.Mu, P, FunctorProfunctor.Mu<CocartesianLike.Mu>> toFP() {
      return new FunctorProfunctor<CocartesianLike.Mu, P, FunctorProfunctor.Mu<CocartesianLike.Mu>>() {
         public <A, B, F extends K1> App2<P, App<F, A>, App<F, B>> distribute(App<? extends CocartesianLike.Mu, F> var1, App2<P, A, B> var2) {
            return this.cap(CocartesianLike.unbox(var1), var2);
         }

         private <A, B, F extends K1, C> App2<P, App<F, A>, App<F, B>> cap(CocartesianLike<F, C, ?> var1, App2<P, A, B> var2) {
            Cocartesian var10000 = Cocartesian.this;
            App2 var10001 = Cocartesian.this.left(var2);
            Function var10002 = (var1x) -> {
               return Either.unbox(var1.to(var1x));
            };
            var1.getClass();
            return var10000.dimap(var10001, var10002, var1::from);
         }
      };
   }

   public interface Mu extends Profunctor.Mu {
      TypeToken<Cocartesian.Mu> TYPE_TOKEN = new TypeToken<Cocartesian.Mu>() {
      };
   }
}
