package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Function;

public interface ForgetOpt<R, A, B> extends App2<ForgetOpt.Mu<R>, A, B> {
   static <R, A, B> ForgetOpt<R, A, B> unbox(App2<ForgetOpt.Mu<R>, A, B> var0) {
      return (ForgetOpt)var0;
   }

   Optional<R> run(A var1);

   public static final class Instance<R> implements AffineP<ForgetOpt.Mu<R>, ForgetOpt.Instance.Mu<R>>, App<ForgetOpt.Instance.Mu<R>, ForgetOpt.Mu<R>> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<ForgetOpt.Mu<R>, A, B>, App2<ForgetOpt.Mu<R>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var1x) -> {
            return Optics.forgetOpt((var2) -> {
               return ForgetOpt.unbox(var1x).run(var1.apply(var2));
            });
         };
      }

      public <A, B, C> App2<ForgetOpt.Mu<R>, Pair<A, C>, Pair<B, C>> first(App2<ForgetOpt.Mu<R>, A, B> var1) {
         return Optics.forgetOpt((var1x) -> {
            return ForgetOpt.unbox(var1).run(var1x.getFirst());
         });
      }

      public <A, B, C> App2<ForgetOpt.Mu<R>, Pair<C, A>, Pair<C, B>> second(App2<ForgetOpt.Mu<R>, A, B> var1) {
         return Optics.forgetOpt((var1x) -> {
            return ForgetOpt.unbox(var1).run(var1x.getSecond());
         });
      }

      public <A, B, C> App2<ForgetOpt.Mu<R>, Either<A, C>, Either<B, C>> left(App2<ForgetOpt.Mu<R>, A, B> var1) {
         return Optics.forgetOpt((var1x) -> {
            Optional var10000 = var1x.left();
            ForgetOpt var10001 = ForgetOpt.unbox(var1);
            var10001.getClass();
            return var10000.flatMap(var10001::run);
         });
      }

      public <A, B, C> App2<ForgetOpt.Mu<R>, Either<C, A>, Either<C, B>> right(App2<ForgetOpt.Mu<R>, A, B> var1) {
         return Optics.forgetOpt((var1x) -> {
            Optional var10000 = var1x.right();
            ForgetOpt var10001 = ForgetOpt.unbox(var1);
            var10001.getClass();
            return var10000.flatMap(var10001::run);
         });
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
