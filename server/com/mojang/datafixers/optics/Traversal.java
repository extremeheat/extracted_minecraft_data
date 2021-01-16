package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import java.util.function.Function;

public interface Traversal<S, T, A, B> extends Wander<S, T, A, B>, App2<Traversal.Mu<A, B>, S, T>, Optic<TraversalP.Mu, S, T, A, B> {
   static <S, T, A, B> Traversal<S, T, A, B> unbox(App2<Traversal.Mu<A, B>, S, T> var0) {
      return (Traversal)var0;
   }

   default <P extends K2> FunctionType<App2<P, A, B>, App2<P, S, T>> eval(App<? extends TraversalP.Mu, P> var1) {
      TraversalP var2 = TraversalP.unbox(var1);
      return (var2x) -> {
         return var2.wander(this, var2x);
      };
   }

   public static final class Instance<A2, B2> implements TraversalP<Traversal.Mu<A2, B2>, TraversalP.Mu> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<Traversal.Mu<A2, B2>, A, B>, App2<Traversal.Mu<A2, B2>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var3) -> {
            return new Traversal<C, D, A2, B2>() {
               public <F extends K1> FunctionType<C, App<F, D>> wander(Applicative<F, ?> var1x, FunctionType<A2, App<F, B2>> var2x) {
                  return (var5) -> {
                     return var1x.map(var1, (App)Traversal.unbox(var3).wander(var1x, var2x).apply(var2.apply(var5)));
                  };
               }
            };
         };
      }

      public <S, T, A, B> App2<Traversal.Mu<A2, B2>, S, T> wander(final Wander<S, T, A, B> var1, final App2<Traversal.Mu<A2, B2>, A, B> var2) {
         return new Traversal<S, T, A2, B2>() {
            public <F extends K1> FunctionType<S, App<F, T>> wander(Applicative<F, ?> var1x, FunctionType<A2, App<F, B2>> var2x) {
               return var1.wander(var1x, Traversal.unbox(var2).wander(var1x, var2x));
            }
         };
      }
   }

   public static final class Mu<A, B> implements K2 {
      public Mu() {
         super();
      }
   }
}
