package com.mojang.datafixers.kinds;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ListBox<T> implements App<ListBox.Mu, T> {
   private final List<T> value;

   public static <T> List<T> unbox(App<ListBox.Mu, T> var0) {
      return ((ListBox)var0).value;
   }

   public static <T> ListBox<T> create(List<T> var0) {
      return new ListBox(var0);
   }

   private ListBox(List<T> var1) {
      super();
      this.value = var1;
   }

   public static <F extends K1, A, B> App<F, List<B>> traverse(Applicative<F, ?> var0, Function<A, App<F, B>> var1, List<A> var2) {
      return var0.map(ListBox::unbox, ListBox.Instance.INSTANCE.traverse(var0, var1, create(var2)));
   }

   public static <F extends K1, A> App<F, List<A>> flip(Applicative<F, ?> var0, List<App<F, A>> var1) {
      return var0.map(ListBox::unbox, ListBox.Instance.INSTANCE.flip(var0, create(var1)));
   }

   public static enum Instance implements Traversable<ListBox.Mu, ListBox.Instance.Mu> {
      INSTANCE;

      private Instance() {
      }

      public <T, R> App<ListBox.Mu, R> map(Function<? super T, ? extends R> var1, App<ListBox.Mu, T> var2) {
         return ListBox.create((List)ListBox.unbox(var2).stream().map(var1).collect(Collectors.toList()));
      }

      public <F extends K1, A, B> App<F, App<ListBox.Mu, B>> traverse(Applicative<F, ?> var1, Function<A, App<F, B>> var2, App<ListBox.Mu, A> var3) {
         List var4 = ListBox.unbox(var3);
         App var5 = var1.point(ImmutableList.builder());

         App var8;
         for(Iterator var6 = var4.iterator(); var6.hasNext(); var5 = var1.ap2(var1.point(ImmutableList.Builder::add), var5, var8)) {
            Object var7 = var6.next();
            var8 = (App)var2.apply(var7);
         }

         return var1.map((var0) -> {
            return ListBox.create(var0.build());
         }, var5);
      }

      public static final class Mu implements Traversable.Mu {
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
