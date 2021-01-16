package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

interface ForgetE<R, A, B> extends App2<ForgetE.Mu<R>, A, B> {
   static <R, A, B> ForgetE<R, A, B> unbox(App2<ForgetE.Mu<R>, A, B> var0) {
      return (ForgetE)var0;
   }

   Either<B, R> run(A var1);

   public static final class Instance<R> implements AffineP<ForgetE.Mu<R>, ForgetE.Instance.Mu<R>>, App<ForgetE.Instance.Mu<R>, ForgetE.Mu<R>> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<ForgetE.Mu<R>, A, B>, App2<ForgetE.Mu<R>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return Optics.forgetE((var3) -> {
               return ForgetE.unbox(var2x).run(var1.apply(var3)).mapLeft(var2);
            });
         };
      }

      public <A, B, C> App2<ForgetE.Mu<R>, Pair<A, C>, Pair<B, C>> first(App2<ForgetE.Mu<R>, A, B> var1) {
         return Optics.forgetE((var1x) -> {
            return ForgetE.unbox(var1).run(var1x.getFirst()).mapLeft((var1xx) -> {
               return Pair.of(var1xx, var1x.getSecond());
            });
         });
      }

      public <A, B, C> App2<ForgetE.Mu<R>, Pair<C, A>, Pair<C, B>> second(App2<ForgetE.Mu<R>, A, B> var1) {
         return Optics.forgetE((var1x) -> {
            return ForgetE.unbox(var1).run(var1x.getSecond()).mapLeft((var1xx) -> {
               return Pair.of(var1x.getFirst(), var1xx);
            });
         });
      }

      public <A, B, C> App2<ForgetE.Mu<R>, Either<A, C>, Either<B, C>> left(App2<ForgetE.Mu<R>, A, B> var1) {
         return Optics.forgetE((var1x) -> {
            return (Either)var1x.map((var1xx) -> {
               return ForgetE.unbox(var1).run(var1xx).mapLeft(Either::left);
            }, (var0) -> {
               return Either.left(Either.right(var0));
            });
         });
      }

      public <A, B, C> App2<ForgetE.Mu<R>, Either<C, A>, Either<C, B>> right(App2<ForgetE.Mu<R>, A, B> var1) {
         return Optics.forgetE((var1x) -> {
            return (Either)var1x.map((var0) -> {
               return Either.left(Either.left(var0));
            }, (var1xx) -> {
               return ForgetE.unbox(var1).run(var1xx).mapLeft(Either::right);
            });
         });
      }

      static final class Mu<R> implements AffineP.Mu {
         Mu() {
            super();
         }
      }
   }

   public static final class Mu<R> implements K2 {
      public Mu() {
         super();
      }
   }
}
