package com.mojang.datafixers.kinds;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class OptionalBox<T> implements App<OptionalBox.Mu, T> {
   private final Optional<T> value;

   public static <T> Optional<T> unbox(App<OptionalBox.Mu, T> var0) {
      return ((OptionalBox)var0).value;
   }

   public static <T> OptionalBox<T> create(Optional<T> var0) {
      return new OptionalBox(var0);
   }

   private OptionalBox(Optional<T> var1) {
      super();
      this.value = var1;
   }

   public static enum Instance implements Applicative<OptionalBox.Mu, OptionalBox.Instance.Mu>, Traversable<OptionalBox.Mu, OptionalBox.Instance.Mu> {
      INSTANCE;

      private Instance() {
      }

      public <T, R> App<OptionalBox.Mu, R> map(Function<? super T, ? extends R> var1, App<OptionalBox.Mu, T> var2) {
         return OptionalBox.create(OptionalBox.unbox(var2).map(var1));
      }

      public <A> App<OptionalBox.Mu, A> point(A var1) {
         return OptionalBox.create(Optional.of(var1));
      }

      public <A, R> Function<App<OptionalBox.Mu, A>, App<OptionalBox.Mu, R>> lift1(App<OptionalBox.Mu, Function<A, R>> var1) {
         return (var1x) -> {
            return OptionalBox.create(OptionalBox.unbox(var1).flatMap((var1xx) -> {
               return OptionalBox.unbox(var1x).map(var1xx);
            }));
         };
      }

      public <A, B, R> BiFunction<App<OptionalBox.Mu, A>, App<OptionalBox.Mu, B>, App<OptionalBox.Mu, R>> lift2(App<OptionalBox.Mu, BiFunction<A, B, R>> var1) {
         return (var1x, var2) -> {
            return OptionalBox.create(OptionalBox.unbox(var1).flatMap((var2x) -> {
               return OptionalBox.unbox(var1x).flatMap((var2xx) -> {
                  return OptionalBox.unbox(var2).map((var2xxx) -> {
                     return var2x.apply(var2xx, var2xxx);
                  });
               });
            }));
         };
      }

      public <F extends K1, A, B> App<F, App<OptionalBox.Mu, B>> traverse(Applicative<F, ?> var1, Function<A, App<F, B>> var2, App<OptionalBox.Mu, A> var3) {
         Optional var4 = OptionalBox.unbox(var3).map(var2);
         return var4.isPresent() ? var1.map((var0) -> {
            return OptionalBox.create(Optional.of(var0));
         }, (App)var4.get()) : var1.point(OptionalBox.create(Optional.empty()));
      }

      public static final class Mu implements Applicative.Mu, Traversable.Mu {
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
