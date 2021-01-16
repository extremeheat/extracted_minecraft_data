package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.Profunctor;
import java.util.function.Function;

public interface Adapter<S, T, A, B> extends App2<Adapter.Mu<A, B>, S, T>, Optic<Profunctor.Mu, S, T, A, B> {
   static <S, T, A, B> Adapter<S, T, A, B> unbox(App2<Adapter.Mu<A, B>, S, T> var0) {
      return (Adapter)var0;
   }

   A from(S var1);

   T to(B var1);

   default <P extends K2> FunctionType<App2<P, A, B>, App2<P, S, T>> eval(App<? extends Profunctor.Mu, P> var1) {
      Profunctor var2 = Profunctor.unbox(var1);
      return (var2x) -> {
         return var2.dimap(var2x, this::from, this::to);
      };
   }

   public static final class Instance<A2, B2> implements Profunctor<Adapter.Mu<A2, B2>, Profunctor.Mu> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<Adapter.Mu<A2, B2>, A, B>, App2<Adapter.Mu<A2, B2>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return Optics.adapter((var2xx) -> {
               return Adapter.unbox(var2x).from(var1.apply(var2xx));
            }, (var2xx) -> {
               return var2.apply(Adapter.unbox(var2x).to(var2xx));
            });
         };
      }
   }

   public static final class Mu<A, B> implements K2 {
      public Mu() {
         super();
      }
   }
}
