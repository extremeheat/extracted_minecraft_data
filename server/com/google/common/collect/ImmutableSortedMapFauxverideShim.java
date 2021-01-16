package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

@GwtIncompatible
abstract class ImmutableSortedMapFauxverideShim<K, V> extends ImmutableMap<K, V> {
   ImmutableSortedMapFauxverideShim() {
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

   /** @deprecated */
   @Deprecated
   public static <K, V> ImmutableSortedMap.Builder<K, V> builder() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      throw new UnsupportedOperationException();
   }
}
