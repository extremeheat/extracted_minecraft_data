package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.Cartesian;
import com.mojang.datafixers.optics.profunctors.ReCocartesian;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

public interface Forget<R, A, B> extends App2<Forget.Mu<R>, A, B> {
   static <R, A, B> Forget<R, A, B> unbox(App2<Forget.Mu<R>, A, B> var0) {
      return (Forget)var0;
   }

   R run(A var1);

   public static final class Instance<R> implements Cartesian<Forget.Mu<R>, Forget.Instance.Mu<R>>, ReCocartesian<Forget.Mu<R>, Forget.Instance.Mu<R>>, App<Forget.Instance.Mu<R>, Forget.Mu<R>> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<Forget.Mu<R>, A, B>, App2<Forget.Mu<R>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var1x) -> {
            return Optics.forget((var2) -> {
               return Forget.unbox(var1x).run(var1.apply(var2));
            });
         };
      }

      public <A, B, C> App2<Forget.Mu<R>, Pair<A, C>, Pair<B, C>> first(App2<Forget.Mu<R>, A, B> var1) {
         return Optics.forget((var1x) -> {
            return Forget.unbox(var1).run(var1x.getFirst());
         });
      }

      public <A, B, C> App2<Forget.Mu<R>, Pair<C, A>, Pair<C, B>> second(App2<Forget.Mu<R>, A, B> var1) {
         return Optics.forget((var1x) -> {
            return Forget.unbox(var1).run(var1x.getSecond());
         });
      }

      public <A, B, C> App2<Forget.Mu<R>, A, B> unleft(App2<Forget.Mu<R>, Either<A, C>, Either<B, C>> var1) {
         return Optics.forget((var1x) -> {
            return Forget.unbox(var1).run(Either.left(var1x));
         });
      }

      public <A, B, C> App2<Forget.Mu<R>, A, B> unright(App2<Forget.Mu<R>, Either<C, A>, Either<C, B>> var1) {
         return Optics.forget((var1x) -> {
            return Forget.unbox(var1).run(Either.right(var1x));
         });
      }

      public static final class Mu<R> implements Cartesian.Mu, ReCocartesian.Mu {
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
