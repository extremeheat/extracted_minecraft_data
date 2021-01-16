package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.Cocartesian;
import com.mojang.datafixers.optics.profunctors.ReCartesian;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

interface ReForget<R, A, B> extends App2<ReForget.Mu<R>, A, B> {
   static <R, A, B> ReForget<R, A, B> unbox(App2<ReForget.Mu<R>, A, B> var0) {
      return (ReForget)var0;
   }

   B run(R var1);

   public static final class Instance<R> implements ReCartesian<ReForget.Mu<R>, ReForget.Instance.Mu<R>>, Cocartesian<ReForget.Mu<R>, ReForget.Instance.Mu<R>>, App<ReForget.Instance.Mu<R>, ReForget.Mu<R>> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<ReForget.Mu<R>, A, B>, App2<ReForget.Mu<R>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var1x) -> {
            return Optics.reForget((var2x) -> {
               return var2.apply(ReForget.unbox(var1x).run(var2x));
            });
         };
      }

      public <A, B, C> App2<ReForget.Mu<R>, A, B> unfirst(App2<ReForget.Mu<R>, Pair<A, C>, Pair<B, C>> var1) {
         return Optics.reForget((var1x) -> {
            return ((Pair)ReForget.unbox(var1).run(var1x)).getFirst();
         });
      }

      public <A, B, C> App2<ReForget.Mu<R>, A, B> unsecond(App2<ReForget.Mu<R>, Pair<C, A>, Pair<C, B>> var1) {
         return Optics.reForget((var1x) -> {
            return ((Pair)ReForget.unbox(var1).run(var1x)).getSecond();
         });
      }

      public <A, B, C> App2<ReForget.Mu<R>, Either<A, C>, Either<B, C>> left(App2<ReForget.Mu<R>, A, B> var1) {
         return Optics.reForget((var1x) -> {
            return Either.left(ReForget.unbox(var1).run(var1x));
         });
      }

      public <A, B, C> App2<ReForget.Mu<R>, Either<C, A>, Either<C, B>> right(App2<ReForget.Mu<R>, A, B> var1) {
         return Optics.reForget((var1x) -> {
            return Either.right(ReForget.unbox(var1).run(var1x));
         });
      }

      static final class Mu<R> implements ReCartesian.Mu, Cocartesian.Mu {
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
