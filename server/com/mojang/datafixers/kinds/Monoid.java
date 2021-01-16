package com.mojang.datafixers.kinds;

import com.google.common.collect.ImmutableList;
import java.util.List;

public interface Monoid<T> {
   T point();

   T add(T var1, T var2);

   static <T> Monoid<List<T>> listMonoid() {
      return new Monoid<List<T>>() {
         public List<T> point() {
            return ImmutableList.of();
         }

         public List<T> add(List<T> var1, List<T> var2) {
            ImmutableList.Builder var3 = ImmutableList.builder();
            var3.addAll((Iterable)var1);
            var3.addAll((Iterable)var2);
            return var3.build();
         }
      };
   }
}
