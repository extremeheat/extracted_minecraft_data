package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

@GwtCompatible
final class CollectCollectors {
   CollectCollectors() {
      super();
   }

   static <T, K, V> Collector<T, ?, ImmutableBiMap<K, V>> toImmutableBiMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return Collector.of(ImmutableBiMap.Builder::new, (var2, var3) -> {
         var2.put(var0.apply(var3), var1.apply(var3));
      }, ImmutableBiMap.Builder::combine, ImmutableBiMap.Builder::build);
   }

   static <E> Collector<E, ?, ImmutableList<E>> toImmutableList() {
      return Collector.of(ImmutableList::builder, ImmutableList.Builder::add, ImmutableList.Builder::combine, ImmutableList.Builder::build);
   }

   static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return Collector.of(ImmutableMap.Builder::new, (var2, var3) -> {
         var2.put(var0.apply(var3), var1.apply(var3));
      }, ImmutableMap.Builder::combine, ImmutableMap.Builder::build);
   }

   static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
      return Collector.of(ImmutableSet::builder, ImmutableSet.Builder::add, ImmutableSet.Builder::combine, ImmutableSet.Builder::build);
   }

   static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(Comparator<? super K> var0, Function<? super T, ? extends K> var1, Function<? super T, ? extends V> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return Collector.of(() -> {
         return new ImmutableSortedMap.Builder(var0);
      }, (var2x, var3) -> {
         var2x.put(var1.apply(var3), var2.apply(var3));
      }, ImmutableSortedMap.Builder::combine, ImmutableSortedMap.Builder::build, Characteristics.UNORDERED);
   }

   static <E> Collector<E, ?, ImmutableSortedSet<E>> toImmutableSortedSet(Comparator<? super E> var0) {
      Preconditions.checkNotNull(var0);
      return Collector.of(() -> {
         return new ImmutableSortedSet.Builder(var0);
      }, ImmutableSortedSet.Builder::add, ImmutableSortedSet.Builder::combine, ImmutableSortedSet.Builder::build);
   }
}
