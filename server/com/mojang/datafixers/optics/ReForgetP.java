package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

interface ReForgetP<R, A, B> extends App2<ReForgetP.Mu<R>, A, B> {
   static <R, A, B> ReForgetP<R, A, B> unbox(App2<ReForgetP.Mu<R>, A, B> var0) {
      return (ReForgetP)var0;
   }

   B run(A var1, R var2);

   public static final class Instance<R> implements AffineP<ReForgetP.Mu<R>, ReForgetP.Instance.Mu<R>>, App<ReForgetP.Instance.Mu<R>, ReForgetP.Mu<R>> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<ReForgetP.Mu<R>, A, B>, App2<ReForgetP.Mu<R>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return Optics.reForgetP("dimap", (var3, var4) -> {
               Object var5 = var1.apply(var3);
               Object var6 = ReForgetP.unbox(var2x).run(var5, var4);
               Object var7 = var2.apply(var6);
               return var7;
            });
         };
      }

      public <A, B, C> App2<ReForgetP.Mu<R>, Either<A, C>, Either<B, C>> left(App2<ReForgetP.Mu<R>, A, B> var1) {
         return Optics.reForgetP("left", (var1x, var2) -> {
            return var1x.mapLeft((var2x) -> {
               return ReForgetP.unbox(var1).run(var2x, var2);
            });
         });
      }

      public <A, B, C> App2<ReForgetP.Mu<R>, Either<C, A>, Either<C, B>> right(App2<ReForgetP.Mu<R>, A, B> var1) {
         return Optics.reForgetP("right", (var1x, var2) -> {
            return var1x.mapRight((var2x) -> {
               return ReForgetP.unbox(var1).run(var2x, var2);
            });
         });
      }

      public <A, B, C> App2<ReForgetP.Mu<R>, Pair<A, C>, Pair<B, C>> first(App2<ReForgetP.Mu<R>, A, B> var1) {
         return Optics.reForgetP("first", (var1x, var2) -> {
            return Pair.of(ReForgetP.unbox(var1).run(var1x.getFirst(), var2), var1x.getSecond());
         });
      }

      public <A, B, C> App2<ReForgetP.Mu<R>, Pair<C, A>, Pair<C, B>> second(App2<ReForgetP.Mu<R>, A, B> var1) {
         return Optics.reForgetP("second", (var1x, var2) -> {
            return Pair.of(var1x.getFirst(), ReForgetP.unbox(var1).run(var1x.getSecond(), var2));
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
