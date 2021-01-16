package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.Cocartesian;
import com.mojang.datafixers.util.Either;
import java.util.function.Function;

public interface Prism<S, T, A, B> extends App2<Prism.Mu<A, B>, S, T>, Optic<Cocartesian.Mu, S, T, A, B> {
   static <S, T, A, B> Prism<S, T, A, B> unbox(App2<Prism.Mu<A, B>, S, T> var0) {
      return (Prism)var0;
   }

   Either<T, A> match(S var1);

   T build(B var1);

   default <P extends K2> FunctionType<App2<P, A, B>, App2<P, S, T>> eval(App<? extends Cocartesian.Mu, P> var1) {
      Cocartesian var2 = Cocartesian.unbox(var1);
      return (var2x) -> {
         return var2.dimap(var2.right(var2x), this::match, (var1) -> {
            return var1.map(Function.identity(), this::build);
         });
      };
   }

   public static final class Instance<A2, B2> implements Cocartesian<Prism.Mu<A2, B2>, Cocartesian.Mu> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<Prism.Mu<A2, B2>, A, B>, App2<Prism.Mu<A2, B2>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return Optics.prism((var3) -> {
               return Prism.unbox(var2x).match(var1.apply(var3)).mapLeft(var2);
            }, (var2xx) -> {
               return var2.apply(Prism.unbox(var2x).build(var2xx));
            });
         };
      }

      public <A, B, C> App2<Prism.Mu<A2, B2>, Either<A, C>, Either<B, C>> left(App2<Prism.Mu<A2, B2>, A, B> var1) {
         Prism var2 = Prism.unbox(var1);
         return Optics.prism((var1x) -> {
            return (Either)var1x.map((var1) -> {
               return var2.match(var1).mapLeft(Either::left);
            }, (var0) -> {
               return Either.left(Either.right(var0));
            });
         }, (var1x) -> {
            return Either.left(var2.build(var1x));
         });
      }

      public <A, B, C> App2<Prism.Mu<A2, B2>, Either<C, A>, Either<C, B>> right(App2<Prism.Mu<A2, B2>, A, B> var1) {
         Prism var2 = Prism.unbox(var1);
         return Optics.prism((var1x) -> {
            return (Either)var1x.map((var0) -> {
               return Either.left(Either.left(var0));
            }, (var1) -> {
               return var2.match(var1).mapLeft(Either::right);
            });
         }, (var1x) -> {
            return Either.right(var2.build(var1x));
         });
      }
   }

   public static final class Mu<A, B> implements K2 {
      public Mu() {
         super();
      }
   }
}
