package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

interface ReForgetEP<R, A, B> extends App2<ReForgetEP.Mu<R>, A, B> {
   static <R, A, B> ReForgetEP<R, A, B> unbox(App2<ReForgetEP.Mu<R>, A, B> var0) {
      return (ReForgetEP)var0;
   }

   B run(Either<A, Pair<A, R>> var1);

   public static final class Instance<R> implements AffineP<ReForgetEP.Mu<R>, ReForgetEP.Instance.Mu<R>>, App<ReForgetEP.Instance.Mu<R>, ReForgetEP.Mu<R>> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<ReForgetEP.Mu<R>, A, B>, App2<ReForgetEP.Mu<R>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return Optics.reForgetEP("dimap", (var3) -> {
               Either var4 = var3.mapBoth(var1, (var1x) -> {
                  return Pair.of(var1.apply(var1x.getFirst()), var1x.getSecond());
               });
               Object var5 = ReForgetEP.unbox(var2x).run(var4);
               Object var6 = var2.apply(var5);
               return var6;
            });
         };
      }

      public <A, B, C> App2<ReForgetEP.Mu<R>, Either<A, C>, Either<B, C>> left(App2<ReForgetEP.Mu<R>, A, B> var1) {
         ReForgetEP var2 = ReForgetEP.unbox(var1);
         return Optics.reForgetEP("left", (var1x) -> {
            return (Either)var1x.map((var1) -> {
               return var1.mapLeft((var1x) -> {
                  return var2.run(Either.left(var1x));
               });
            }, (var1) -> {
               return ((Either)var1.getFirst()).mapLeft((var2x) -> {
                  return var2.run(Either.right(Pair.of(var2x, var1.getSecond())));
               });
            });
         });
      }

      public <A, B, C> App2<ReForgetEP.Mu<R>, Either<C, A>, Either<C, B>> right(App2<ReForgetEP.Mu<R>, A, B> var1) {
         ReForgetEP var2 = ReForgetEP.unbox(var1);
         return Optics.reForgetEP("right", (var1x) -> {
            return (Either)var1x.map((var1) -> {
               return var1.mapRight((var1x) -> {
                  return var2.run(Either.left(var1x));
               });
            }, (var1) -> {
               return ((Either)var1.getFirst()).mapRight((var2x) -> {
                  return var2.run(Either.right(Pair.of(var2x, var1.getSecond())));
               });
            });
         });
      }

      public <A, B, C> App2<ReForgetEP.Mu<R>, Pair<A, C>, Pair<B, C>> first(App2<ReForgetEP.Mu<R>, A, B> var1) {
         ReForgetEP var2 = ReForgetEP.unbox(var1);
         return Optics.reForgetEP("first", (var1x) -> {
            return (Pair)var1x.map((var1) -> {
               return Pair.of(var2.run(Either.left(var1.getFirst())), var1.getSecond());
            }, (var1) -> {
               return Pair.of(var2.run(Either.right(Pair.of(((Pair)var1.getFirst()).getFirst(), var1.getSecond()))), ((Pair)var1.getFirst()).getSecond());
            });
         });
      }

      public <A, B, C> App2<ReForgetEP.Mu<R>, Pair<C, A>, Pair<C, B>> second(App2<ReForgetEP.Mu<R>, A, B> var1) {
         ReForgetEP var2 = ReForgetEP.unbox(var1);
         return Optics.reForgetEP("second", (var1x) -> {
            return (Pair)var1x.map((var1) -> {
               return Pair.of(var1.getFirst(), var2.run(Either.left(var1.getSecond())));
            }, (var1) -> {
               return Pair.of(((Pair)var1.getFirst()).getFirst(), var2.run(Either.right(Pair.of(((Pair)var1.getFirst()).getSecond(), var1.getSecond()))));
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
