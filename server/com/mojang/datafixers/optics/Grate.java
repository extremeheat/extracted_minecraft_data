package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.Closed;
import java.util.function.Function;

interface Grate<S, T, A, B> extends App2<Grate.Mu<A, B>, S, T>, Optic<Closed.Mu, S, T, A, B> {
   static <S, T, A, B> Grate<S, T, A, B> unbox(App2<Grate.Mu<A, B>, S, T> var0) {
      return (Grate)var0;
   }

   T grate(FunctionType<FunctionType<S, A>, B> var1);

   default <P extends K2> FunctionType<App2<P, A, B>, App2<P, S, T>> eval(App<? extends Closed.Mu, P> var1) {
      Closed var2 = Closed.unbox(var1);
      return (var2x) -> {
         return var2.dimap(var2.closed(var2x), (var0) -> {
            return (var1) -> {
               return var1.apply(var0);
            };
         }, this::grate);
      };
   }

   public static final class Instance<A2, B2> implements Closed<Grate.Mu<A2, B2>, Closed.Mu> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<Grate.Mu<A2, B2>, A, B>, App2<Grate.Mu<A2, B2>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return Optics.grate((var3) -> {
               return var2.apply(Grate.unbox(var2x).grate((var2xx) -> {
                  return var3.apply(FunctionType.create(var2xx.compose(var1)));
               }));
            });
         };
      }

      public <A, B, X> App2<Grate.Mu<A2, B2>, FunctionType<X, A>, FunctionType<X, B>> closed(App2<Grate.Mu<A2, B2>, A, B> var1) {
         FunctionType var2 = (var0) -> {
            return (var1) -> {
               return var0.apply((var1x) -> {
                  return var1x.apply(var1);
               });
            };
         };
         return (App2)Optics.grate(var2).eval(this).apply(Grate.unbox(var1));
      }
   }

   public static final class Mu<A, B> implements K2 {
      public Mu() {
         super();
      }
   }
}
