package com.mojang.datafixers.util;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.CocartesianLike;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.Traversable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Either<L, R> implements App<Either.Mu<R>, L> {
   public static <L, R> Either<L, R> unbox(App<Either.Mu<R>, L> var0) {
      return (Either)var0;
   }

   private Either() {
      super();
   }

   public abstract <C, D> Either<C, D> mapBoth(Function<? super L, ? extends C> var1, Function<? super R, ? extends D> var2);

   public abstract <T> T map(Function<? super L, ? extends T> var1, Function<? super R, ? extends T> var2);

   public abstract Either<L, R> ifLeft(Consumer<? super L> var1);

   public abstract Either<L, R> ifRight(Consumer<? super R> var1);

   public abstract Optional<L> left();

   public abstract Optional<R> right();

   public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> var1) {
      return (Either)this.map((var1x) -> {
         return left(var1.apply(var1x));
      }, Either::right);
   }

   public <T> Either<L, T> mapRight(Function<? super R, ? extends T> var1) {
      return (Either)this.map(Either::left, (var1x) -> {
         return right(var1.apply(var1x));
      });
   }

   public static <L, R> Either<L, R> left(L var0) {
      return new Either.Left(var0);
   }

   public static <L, R> Either<L, R> right(R var0) {
      return new Either.Right(var0);
   }

   public L orThrow() {
      return this.map((var0) -> {
         return var0;
      }, (var0) -> {
         if (var0 instanceof Throwable) {
            throw new RuntimeException((Throwable)var0);
         } else {
            throw new RuntimeException(var0.toString());
         }
      });
   }

   public Either<R, L> swap() {
      return (Either)this.map(Either::right, Either::left);
   }

   public <L2> Either<L2, R> flatMap(Function<L, Either<L2, R>> var1) {
      return (Either)this.map(var1, Either::right);
   }

   // $FF: synthetic method
   Either(Object var1) {
      this();
   }

   public static final class Instance<R2> implements Applicative<Either.Mu<R2>, Either.Instance.Mu<R2>>, Traversable<Either.Mu<R2>, Either.Instance.Mu<R2>>, CocartesianLike<Either.Mu<R2>, R2, Either.Instance.Mu<R2>> {
      public Instance() {
         super();
      }

      public <T, R> App<Either.Mu<R2>, R> map(Function<? super T, ? extends R> var1, App<Either.Mu<R2>, T> var2) {
         return Either.unbox(var2).mapLeft(var1);
      }

      public <A> App<Either.Mu<R2>, A> point(A var1) {
         return Either.left(var1);
      }

      public <A, R> Function<App<Either.Mu<R2>, A>, App<Either.Mu<R2>, R>> lift1(App<Either.Mu<R2>, Function<A, R>> var1) {
         return (var1x) -> {
            return Either.unbox(var1).flatMap((var1xx) -> {
               return Either.unbox(var1x).mapLeft(var1xx);
            });
         };
      }

      public <A, B, R> BiFunction<App<Either.Mu<R2>, A>, App<Either.Mu<R2>, B>, App<Either.Mu<R2>, R>> lift2(App<Either.Mu<R2>, BiFunction<A, B, R>> var1) {
         return (var1x, var2) -> {
            return Either.unbox(var1).flatMap((var2x) -> {
               return Either.unbox(var1x).flatMap((var2xx) -> {
                  return Either.unbox(var2).mapLeft((var2xxx) -> {
                     return var2x.apply(var2xx, var2xxx);
                  });
               });
            });
         };
      }

      public <F extends K1, A, B> App<F, App<Either.Mu<R2>, B>> traverse(Applicative<F, ?> var1, Function<A, App<F, B>> var2, App<Either.Mu<R2>, A> var3) {
         return (App)Either.unbox(var3).map((var2x) -> {
            App var3 = (App)var2.apply(var2x);
            return var1.ap(Either::left, var3);
         }, (var1x) -> {
            return var1.point(Either.right(var1x));
         });
      }

      public <A> App<Either.Mu<R2>, A> to(App<Either.Mu<R2>, A> var1) {
         return var1;
      }

      public <A> App<Either.Mu<R2>, A> from(App<Either.Mu<R2>, A> var1) {
         return var1;
      }

      public static final class Mu<R2> implements Applicative.Mu, Traversable.Mu, CocartesianLike.Mu {
         public Mu() {
            super();
         }
      }
   }

   private static final class Right<L, R> extends Either<L, R> {
      private final R value;

      public Right(R var1) {
         super(null);
         this.value = var1;
      }

      public <C, D> Either<C, D> mapBoth(Function<? super L, ? extends C> var1, Function<? super R, ? extends D> var2) {
         return new Either.Right(var2.apply(this.value));
      }

      public <T> T map(Function<? super L, ? extends T> var1, Function<? super R, ? extends T> var2) {
         return var2.apply(this.value);
      }

      public Either<L, R> ifLeft(Consumer<? super L> var1) {
         return this;
      }

      public Either<L, R> ifRight(Consumer<? super R> var1) {
         var1.accept(this.value);
         return this;
      }

      public Optional<L> left() {
         return Optional.empty();
      }

      public Optional<R> right() {
         return Optional.of(this.value);
      }

      public String toString() {
         return "Right[" + this.value + "]";
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            Either.Right var2 = (Either.Right)var1;
            return Objects.equals(this.value, var2.value);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.value});
      }
   }

   private static final class Left<L, R> extends Either<L, R> {
      private final L value;

      public Left(L var1) {
         super(null);
         this.value = var1;
      }

      public <C, D> Either<C, D> mapBoth(Function<? super L, ? extends C> var1, Function<? super R, ? extends D> var2) {
         return new Either.Left(var1.apply(this.value));
      }

      public <T> T map(Function<? super L, ? extends T> var1, Function<? super R, ? extends T> var2) {
         return var1.apply(this.value);
      }

      public Either<L, R> ifLeft(Consumer<? super L> var1) {
         var1.accept(this.value);
         return this;
      }

      public Either<L, R> ifRight(Consumer<? super R> var1) {
         return this;
      }

      public Optional<L> left() {
         return Optional.of(this.value);
      }

      public Optional<R> right() {
         return Optional.empty();
      }

      public String toString() {
         return "Left[" + this.value + "]";
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            Either.Left var2 = (Either.Left)var1;
            return Objects.equals(this.value, var2.value);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.value});
      }
   }

   public static final class Mu<R> implements K1 {
      public Mu() {
         super();
      }
   }
}
