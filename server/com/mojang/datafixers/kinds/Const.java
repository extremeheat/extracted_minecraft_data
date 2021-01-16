package com.mojang.datafixers.kinds;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class Const<C, T> implements App<Const.Mu<C>, T> {
   private final C value;

   public static <C, T> C unbox(App<Const.Mu<C>, T> var0) {
      return ((Const)var0).value;
   }

   public static <C, T> Const<C, T> create(C var0) {
      return new Const(var0);
   }

   Const(C var1) {
      super();
      this.value = var1;
   }

   public static final class Instance<C> implements Applicative<Const.Mu<C>, Const.Instance.Mu<C>> {
      private final Monoid<C> monoid;

      public Instance(Monoid<C> var1) {
         super();
         this.monoid = var1;
      }

      public <T, R> App<Const.Mu<C>, R> map(Function<? super T, ? extends R> var1, App<Const.Mu<C>, T> var2) {
         return Const.create(Const.unbox(var2));
      }

      public <A> App<Const.Mu<C>, A> point(A var1) {
         return Const.create(this.monoid.point());
      }

      public <A, R> Function<App<Const.Mu<C>, A>, App<Const.Mu<C>, R>> lift1(App<Const.Mu<C>, Function<A, R>> var1) {
         return (var2) -> {
            return Const.create(this.monoid.add(Const.unbox(var1), Const.unbox(var2)));
         };
      }

      public <A, B, R> BiFunction<App<Const.Mu<C>, A>, App<Const.Mu<C>, B>, App<Const.Mu<C>, R>> lift2(App<Const.Mu<C>, BiFunction<A, B, R>> var1) {
         return (var2, var3) -> {
            return Const.create(this.monoid.add(Const.unbox(var1), this.monoid.add(Const.unbox(var2), Const.unbox(var3))));
         };
      }

      public static final class Mu<C> implements Applicative.Mu {
         public Mu() {
            super();
         }
      }
   }

   public static final class Mu<C> implements K1 {
      public Mu() {
         super();
      }
   }
}
