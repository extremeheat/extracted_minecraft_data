package com.mojang.datafixers.util;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.CartesianLike;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.Traversable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Pair<F, S> implements App<Pair.Mu<S>, F> {
   private final F first;
   private final S second;

   public static <F, S> Pair<F, S> unbox(App<Pair.Mu<S>, F> var0) {
      return (Pair)var0;
   }

   public Pair(F var1, S var2) {
      super();
      this.first = var1;
      this.second = var2;
   }

   public F getFirst() {
      return this.first;
   }

   public S getSecond() {
      return this.second;
   }

   public Pair<S, F> swap() {
      return of(this.second, this.first);
   }

   public String toString() {
      return "(" + this.first + ", " + this.second + ")";
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Pair)) {
         return false;
      } else {
         Pair var2 = (Pair)var1;
         return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
      }
   }

   public int hashCode() {
      return com.google.common.base.Objects.hashCode(this.first, this.second);
   }

   public <F2> Pair<F2, S> mapFirst(Function<? super F, ? extends F2> var1) {
      return of(var1.apply(this.first), this.second);
   }

   public <S2> Pair<F, S2> mapSecond(Function<? super S, ? extends S2> var1) {
      return of(this.first, var1.apply(this.second));
   }

   public static <F, S> Pair<F, S> of(F var0, S var1) {
      return new Pair(var0, var1);
   }

   public static <F, S> Collector<Pair<F, S>, ?, Map<F, S>> toMap() {
      return Collectors.toMap(Pair::getFirst, Pair::getSecond);
   }

   public static final class Instance<S2> implements Traversable<Pair.Mu<S2>, Pair.Instance.Mu<S2>>, CartesianLike<Pair.Mu<S2>, S2, Pair.Instance.Mu<S2>> {
      public Instance() {
         super();
      }

      public <T, R> App<Pair.Mu<S2>, R> map(Function<? super T, ? extends R> var1, App<Pair.Mu<S2>, T> var2) {
         return Pair.unbox(var2).mapFirst(var1);
      }

      public <F extends K1, A, B> App<F, App<Pair.Mu<S2>, B>> traverse(Applicative<F, ?> var1, Function<A, App<F, B>> var2, App<Pair.Mu<S2>, A> var3) {
         Pair var4 = Pair.unbox(var3);
         return var1.ap((var1x) -> {
            return Pair.of(var1x, var4.second);
         }, (App)var2.apply(var4.first));
      }

      public <A> App<Pair.Mu<S2>, A> to(App<Pair.Mu<S2>, A> var1) {
         return var1;
      }

      public <A> App<Pair.Mu<S2>, A> from(App<Pair.Mu<S2>, A> var1) {
         return var1;
      }

      public static final class Mu<S2> implements Traversable.Mu, CartesianLike.Mu {
         public Mu() {
            super();
         }
      }
   }

   public static final class Mu<S> implements K1 {
      public Mu() {
         super();
      }
   }
}
