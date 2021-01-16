package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;
import com.mojang.datafixers.optics.profunctors.GetterP;
import java.util.function.Function;
import java.util.function.Supplier;

interface Getter<S, T, A, B> extends App2<Getter.Mu<A, B>, S, T>, Optic<GetterP.Mu, S, T, A, B> {
   static <S, T, A, B> Getter<S, T, A, B> unbox(App2<Getter.Mu<A, B>, S, T> var0) {
      return (Getter)var0;
   }

   A get(S var1);

   default <P extends K2> FunctionType<App2<P, A, B>, App2<P, S, T>> eval(App<? extends GetterP.Mu, P> var1) {
      GetterP var2 = GetterP.unbox(var1);
      return (var2x) -> {
         return var2.lmap(var2.secondPhantom(var2x), this::get);
      };
   }

   public static final class Instance<A2, B2> implements GetterP<Getter.Mu<A2, B2>, GetterP.Mu> {
      public Instance() {
         super();
      }

      public <A, B, C, D> FunctionType<App2<Getter.Mu<A2, B2>, A, B>, App2<Getter.Mu<A2, B2>, C, D>> dimap(Function<C, A> var1, Function<B, D> var2) {
         return (var1x) -> {
            Getter var10001 = Getter.unbox(var1x);
            var10001.getClass();
            return Optics.getter(var1.andThen(var10001::get));
         };
      }

      public <A, B, C, D> FunctionType<Supplier<App2<Getter.Mu<A2, B2>, A, B>>, App2<Getter.Mu<A2, B2>, C, D>> cimap(Function<C, A> var1, Function<D, B> var2) {
         return (var1x) -> {
            Getter var10001 = Getter.unbox((App2)var1x.get());
            var10001.getClass();
            return Optics.getter(var1.andThen(var10001::get));
         };
      }
   }

   public static final class Mu<A, B> implements K2 {
      public Mu() {
         super();
      }
   }
}
