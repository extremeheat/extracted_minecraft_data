package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.optics.profunctors.Cartesian;
import com.mojang.datafixers.optics.profunctors.Cocartesian;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

public interface Affine<S, T, A, B> extends App2<Affine.Mu<A, B>, S, T>, Optic<AffineP.Mu, S, T, A, B> {
   static <S, T, A, B> Affine<S, T, A, B> unbox(App2<Affine.Mu<A, B>, S, T> var0) {
      return (Affine)var0;
   }

   Either<T, A> preview(S var1);

   T set(B var1, S var2);

   default <P extends K2> FunctionType<App2<P, A, B>, App2<P, S, T>> eval(App<? extends AffineP.Mu, P> var1) {
      Cartesian var2 = Cartesian.unbox(var1);
      Cocartesian var3 = Cocartesian.unbox(var1);
      return (var3x) -> {
         return var2.dimap(var3.left(var2.rmap(var2.first(var3x), (var1) -> {
            return this.set(var1.getFirst(), var1.getSecond());
         })), (var1) -> {
            return (Either)this.preview(var1).map(Either::right, (var1x) -> {
               return Either.left(Pair.of(var1x, var1));
            });
         }, (var0) -> {
            return var0.map(Function.identity(), Function.identity());
         });
      };
   }

   public static final class Instance<A2, B2> implements AffineP<Affine.Mu<A2, B2>, AffineP.Mu> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<Affine.Mu<A2, B2>, A, B>, App2<Affine.Mu<A2, B2>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return Optics.affine((var3) -> {
               return Affine.unbox(var2x).preview(var1.apply(var3)).mapLeft(var2);
            }, (var3, var4) -> {
               return var2.apply(Affine.unbox(var2x).set(var3, var1.apply(var4)));
            });
         };
      }

      public <A, B, C> App2<Affine.Mu<A2, B2>, Pair<A, C>, Pair<B, C>> first(App2<Affine.Mu<A2, B2>, A, B> var1) {
         Affine var2 = Affine.unbox(var1);
         return Optics.affine((var1x) -> {
            return var2.preview(var1x.getFirst()).mapBoth((var1) -> {
               return Pair.of(var1, var1x.getSecond());
            }, Function.identity());
         }, (var1x, var2x) -> {
            return Pair.of(var2.set(var1x, var2x.getFirst()), var2x.getSecond());
         });
      }

      public <A, B, C> App2<Affine.Mu<A2, B2>, Pair<C, A>, Pair<C, B>> second(App2<Affine.Mu<A2, B2>, A, B> var1) {
         Affine var2 = Affine.unbox(var1);
         return Optics.affine((var1x) -> {
            return var2.preview(var1x.getSecond()).mapBoth((var1) -> {
               return Pair.of(var1x.getFirst(), var1);
            }, Function.identity());
         }, (var1x, var2x) -> {
            return Pair.of(var2x.getFirst(), var2.set(var1x, var2x.getSecond()));
         });
      }

      public <A, B, C> App2<Affine.Mu<A2, B2>, Either<A, C>, Either<B, C>> left(App2<Affine.Mu<A2, B2>, A, B> var1) {
         Affine var2 = Affine.unbox(var1);
         return Optics.affine((var1x) -> {
            return (Either)var1x.map((var1) -> {
               return var2.preview(var1).mapLeft(Either::left);
            }, (var0) -> {
               return Either.left(Either.right(var0));
            });
         }, (var1x, var2x) -> {
            return (Either)var2x.map((var2xx) -> {
               return Either.left(var2.set(var1x, var2xx));
            }, Either::right);
         });
      }

      public <A, B, C> App2<Affine.Mu<A2, B2>, Either<C, A>, Either<C, B>> right(App2<Affine.Mu<A2, B2>, A, B> var1) {
         Affine var2 = Affine.unbox(var1);
         return Optics.affine((var1x) -> {
            return (Either)var1x.map((var0) -> {
               return Either.left(Either.left(var0));
            }, (var1) -> {
               return var2.preview(var1).mapLeft(Either::right);
            });
         }, (var1x, var2x) -> {
            return (Either)var2x.map(Either::left, (var2xx) -> {
               return Either.right(var2.set(var1x, var2xx));
            });
         });
      }
   }

   public static final class Mu<A, B> implements K2 {
      public Mu() {
         super();
      }
   }
}
