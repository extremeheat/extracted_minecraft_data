package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ReForgetC<R, A, B> extends App2<ReForgetC.Mu<R>, A, B> {
   static <R, A, B> ReForgetC<R, A, B> unbox(App2<ReForgetC.Mu<R>, A, B> var0) {
      return (ReForgetC)var0;
   }

   Either<Function<R, B>, BiFunction<A, R, B>> impl();

   default B run(A var1, R var2) {
      return this.impl().map((var1x) -> {
         return var1x.apply(var2);
      }, (var2x) -> {
         return var2x.apply(var1, var2);
      });
   }

   public static final class Instance<R> implements AffineP<ReForgetC.Mu<R>, ReForgetC.Instance.Mu<R>>, App<ReForgetC.Instance.Mu<R>, ReForgetC.Mu<R>> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<ReForgetC.Mu<R>, A, B>, App2<ReForgetC.Mu<R>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return Optics.reForgetC("dimap", (Either)ReForgetC.unbox(var2x).impl().map((var1x) -> {
               return Either.left((var2x) -> {
                  return var2.apply(var1x.apply(var2x));
               });
            }, (var2xx) -> {
               return Either.right((var3, var4) -> {
                  return var2.apply(var2xx.apply(var1.apply(var3), var4));
               });
            }));
         };
      }

      public <A, B, C> App2<ReForgetC.Mu<R>, Pair<A, C>, Pair<B, C>> first(App2<ReForgetC.Mu<R>, A, B> var1) {
         return Optics.reForgetC("first", (Either)ReForgetC.unbox(var1).impl().map((var0) -> {
            return Either.right((var1, var2) -> {
               return Pair.of(var0.apply(var2), var1.getSecond());
            });
         }, (var0) -> {
            return Either.right((var1, var2) -> {
               return Pair.of(var0.apply(var1.getFirst(), var2), var1.getSecond());
            });
         }));
      }

      public <A, B, C> App2<ReForgetC.Mu<R>, Pair<C, A>, Pair<C, B>> second(App2<ReForgetC.Mu<R>, A, B> var1) {
         return Optics.reForgetC("second", (Either)ReForgetC.unbox(var1).impl().map((var0) -> {
            return Either.right((var1, var2) -> {
               return Pair.of(var1.getFirst(), var0.apply(var2));
            });
         }, (var0) -> {
            return Either.right((var1, var2) -> {
               return Pair.of(var1.getFirst(), var0.apply(var1.getSecond(), var2));
            });
         }));
      }

      public <A, B, C> App2<ReForgetC.Mu<R>, Either<A, C>, Either<B, C>> left(App2<ReForgetC.Mu<R>, A, B> var1) {
         return Optics.reForgetC("left", (Either)ReForgetC.unbox(var1).impl().map((var0) -> {
            return Either.left((var1) -> {
               return Either.left(var0.apply(var1));
            });
         }, (var0) -> {
            return Either.right((var1, var2) -> {
               return var1.mapLeft((var2x) -> {
                  return var0.apply(var2x, var2);
               });
            });
         }));
      }

      public <A, B, C> App2<ReForgetC.Mu<R>, Either<C, A>, Either<C, B>> right(App2<ReForgetC.Mu<R>, A, B> var1) {
         return Optics.reForgetC("right", (Either)ReForgetC.unbox(var1).impl().map((var0) -> {
            return Either.left((var1) -> {
               return Either.right(var0.apply(var1));
            });
         }, (var0) -> {
            return Either.right((var1, var2) -> {
               return var1.mapRight((var2x) -> {
                  return var0.apply(var2x, var2);
               });
            });
         }));
      }

      public static final class Mu<R> implements AffineP.Mu {
         public Mu() {
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
