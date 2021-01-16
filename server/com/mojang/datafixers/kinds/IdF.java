package com.mojang.datafixers.kinds;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class IdF<A> implements App<IdF.Mu, A> {
   protected final A value;

   IdF(A var1) {
      super();
      this.value = var1;
   }

   public A value() {
      return this.value;
   }

   public static <A> A get(App<IdF.Mu, A> var0) {
      return ((IdF)var0).value;
   }

   public static <A> IdF<A> create(A var0) {
      return new IdF(var0);
   }

   public static enum Instance implements Functor<IdF.Mu, IdF.Instance.Mu>, Applicative<IdF.Mu, IdF.Instance.Mu> {
      INSTANCE;

      private Instance() {
      }

      public <T, R> App<IdF.Mu, R> map(Function<? super T, ? extends R> var1, App<IdF.Mu, T> var2) {
         IdF var3 = (IdF)var2;
         return new IdF(var1.apply(var3.value));
      }

      public <A> App<IdF.Mu, A> point(A var1) {
         return IdF.create(var1);
      }

      public <A, R> Function<App<IdF.Mu, A>, App<IdF.Mu, R>> lift1(App<IdF.Mu, Function<A, R>> var1) {
         return (var1x) -> {
            return IdF.create(((Function)IdF.get(var1)).apply(IdF.get(var1x)));
         };
      }

      public <A, B, R> BiFunction<App<IdF.Mu, A>, App<IdF.Mu, B>, App<IdF.Mu, R>> lift2(App<IdF.Mu, BiFunction<A, B, R>> var1) {
         return (var1x, var2) -> {
            return IdF.create(((BiFunction)IdF.get(var1)).apply(IdF.get(var1x), IdF.get(var2)));
         };
      }

      public static final class Mu implements Functor.Mu, Applicative.Mu {
         public Mu() {
            super();
         }
      }
   }

   public static final class Mu implements K1 {
      public Mu() {
         super();
      }
   }
}
