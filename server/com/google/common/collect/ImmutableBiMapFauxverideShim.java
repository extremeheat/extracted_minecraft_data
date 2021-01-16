package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

@GwtIncompatible
abstract class ImmutableBiMapFauxverideShim<K, V> extends ImmutableMap<K, V> {
   ImmutableBiMapFauxverideShim() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1, BinaryOperator<V> var2) {
      throw new UnsupportedOperationException();
   }
}
