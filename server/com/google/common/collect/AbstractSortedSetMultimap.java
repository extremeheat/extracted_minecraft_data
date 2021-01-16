package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractSortedSetMultimap<K, V> extends AbstractSetMultimap<K, V> implements SortedSetMultimap<K, V> {
   private static final long serialVersionUID = 430848587173315748L;

   protected AbstractSortedSetMultimap(Map<K, Collection<V>> var1) {
      super(var1);
   }

   abstract SortedSet<V> createCollection();

   SortedSet<V> createUnmodifiableEmptyCollection() {
      Comparator var1 = this.valueComparator();
      return (SortedSet)(var1 == null ? Collections.unmodifiableSortedSet(this.createCollection()) : ImmutableSortedSet.emptySet(this.valueComparator()));
   }

   public SortedSet<V> get(@Nullable K var1) {
      return (SortedSet)super.get(var1);
   }

   @CanIgnoreReturnValue
   public SortedSet<V> removeAll(@Nullable Object var1) {
      return (SortedSet)super.removeAll(var1);
   }

   @CanIgnoreReturnValue
   public SortedSet<V> replaceValues(@Nullable K var1, Iterable<? extends V> var2) {
      return (SortedSet)super.replaceValues(var1, var2);
   }

   public Map<K, Collection<V>> asMap() {
      return super.asMap();
   }

   public Collection<V> values() {
      return super.values();
   }
}
