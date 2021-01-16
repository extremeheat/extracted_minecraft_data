package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.Cocartesian;
import com.mojang.datafixers.util.Either;
import java.util.function.Function;

interface ReForgetE<R, A, B> extends App2<ReForgetE.Mu<R>, A, B> {
   static <R, A, B> ReForgetE<R, A, B> unbox(App2<ReForgetE.Mu<R>, A, B> var0) {
      return (ReForgetE)var0;
   }

   B run(Either<A, R> var1);

   public static final class Instance<R> implements Cocartesian<ReForgetE.Mu<R>, ReForgetE.Instance.Mu<R>>, App<ReForgetE.Instance.Mu<R>, ReForgetE.Mu<R>> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<ReForgetE.Mu<R>, A, B>, App2<ReForgetE.Mu<R>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return Optics.reForgetE("dimap", (var3) -> {
               Either var4 = var3.mapLeft(var1);
               Object var5 = ReForgetE.unbox(var2x).run(var4);
               Object var6 = var2.apply(var5);
               return var6;
            });
         };
      }

      public <A, B, C> App2<ReForgetE.Mu<R>, Either<A, C>, Either<B, C>> left(App2<ReForgetE.Mu<R>, A, B> var1) {
         ReForgetE var2 = ReForgetE.unbox(var1);
         return Optics.reForgetE("left", (var1x) -> {
            return (Either)var1x.map((var1) -> {
               return (Either)var1.map((var1x) -> {
                  return Either.left(var2.run(Either.left(var1x)));
               }, Either::right);
            }, (var1) -> {
               return Either.left(var2.run(Either.right(var1)));
            });
         });
      }

      public <A, B, C> App2<ReForgetE.Mu<R>, Either<C, A>, Either<C, B>> right(App2<ReForgetE.Mu<R>, A, B> var1) {
         ReForgetE var2 = ReForgetE.unbox(var1);
         return Optics.reForgetE("right", (var1x) -> {
            return (Either)var1x.map((var1) -> {
               return (Either)var1.map(Either::left, (var1x) -> {
                  return Either.right(var2.run(Either.left(var1x)));
               });
            }, (var1) -> {
               return Either.right(var2.run(Either.right(var1)));
            });
         });
      }

      static final class Mu<R> implements Cocartesian.Mu {
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
