package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.Cartesian;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;

public interface Lens<S, T, A, B> extends App2<Lens.Mu<A, B>, S, T>, Optic<Cartesian.Mu, S, T, A, B> {
   static <S, T, A, B> Lens<S, T, A, B> unbox(App2<Lens.Mu<A, B>, S, T> var0) {
      return (Lens)var0;
   }

   static <S, T, A, B> Lens<S, T, A, B> unbox2(App2<Lens.Mu2<S, T>, B, A> var0) {
      return ((Lens.Box)var0).lens;
   }

   static <S, T, A, B> App2<Lens.Mu2<S, T>, B, A> box(Lens<S, T, A, B> var0) {
      return new Lens.Box(var0);
   }

   A view(S var1);

   T update(B var1, S var2);

   default <P extends K2> FunctionType<App2<P, A, B>, App2<P, S, T>> eval(App<? extends Cartesian.Mu, P> var1) {
      Cartesian var2 = Cartesian.unbox(var1);
      return (var2x) -> {
         return var2.dimap(var2.first(var2x), (var1) -> {
            return Pair.of(this.view(var1), var1);
         }, (var1) -> {
            return this.update(var1.getFirst(), var1.getSecond());
         });
      };
   }

   public static final class Instance<A2, B2> implements Cartesian<Lens.Mu<A2, B2>, Cartesian.Mu> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<Lens.Mu<A2, B2>, A, B>, App2<Lens.Mu<A2, B2>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var2x) -> {
            return Optics.lens((var2xx) -> {
               return Lens.unbox(var2x).view(var1.apply(var2xx));
            }, (var3, var4) -> {
               return var2.apply(Lens.unbox(var2x).update(var3, var1.apply(var4)));
            });
         };
      }

      public <A, B, C> App2<Lens.Mu<A2, B2>, Pair<A, C>, Pair<B, C>> first(App2<Lens.Mu<A2, B2>, A, B> var1) {
         return Optics.lens((var1x) -> {
            return Lens.unbox(var1).view(var1x.getFirst());
         }, (var1x, var2) -> {
            return Pair.of(Lens.unbox(var1).update(var1x, var2.getFirst()), var2.getSecond());
         });
      }

      public <A, B, C> App2<Lens.Mu<A2, B2>, Pair<C, A>, Pair<C, B>> second(App2<Lens.Mu<A2, B2>, A, B> var1) {
         return Optics.lens((var1x) -> {
            return Lens.unbox(var1).view(var1x.getSecond());
         }, (var1x, var2) -> {
            return Pair.of(var2.getFirst(), Lens.unbox(var1).update(var1x, var2.getSecond()));
         });
      }
   }

   public static final class Box<S, T, A, B> implements App2<Lens.Mu2<S, T>, B, A> {
      private final Lens<S, T, A, B> lens;

      public Box(Lens<S, T, A, B> var1) {
         super();
         this.lens = var1;
      }
   }

   public static final class Mu2<S, T> implements K2 {
      public Mu2() {
         super();
      }
   }

   public static final class Mu<A, B> implements K2 {
      public Mu() {
         super();
      }
   }
}
