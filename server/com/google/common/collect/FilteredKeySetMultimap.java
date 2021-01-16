package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
final class FilteredKeySetMultimap<K, V> extends FilteredKeyMultimap<K, V> implements FilteredSetMultimap<K, V> {
   FilteredKeySetMultimap(SetMultimap<K, V> var1, Predicate<? super K> var2) {
      super(var1, var2);
   }

   public SetMultimap<K, V> unfiltered() {
      return (SetMultimap)this.unfiltered;
   }

   public Set<V> get(K var1) {
      return (Set)super.get(var1);
   }

   public Set<V> removeAll(Object var1) {
      return (Set)super.removeAll(var1);
   }

   public Set<V> replaceValues(K var1, Iterable<? extends V> var2) {
      return (Set)super.replaceValues(var1, var2);
   }

   public Set<Entry<K, V>> entries() {
      return (Set)super.entries();
   }

   Set<Entry<K, V>> createEntries() {
      return new FilteredKeySetMultimap.EntrySet();
   }

   class EntrySet extends FilteredKeyMultimap<K, V>.Entries implements Set<Entry<K, V>> {
      EntrySet() {
         super();
      }

      public int hashCode() {
         return Sets.hashCodeImpl(this);
      }

      public boolean equals(@Nullable Object var1) {
         return Sets.equalsImpl(this, var1);
      }
   }
}
