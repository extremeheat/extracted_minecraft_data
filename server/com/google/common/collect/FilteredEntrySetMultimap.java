package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import java.util.Set;
import java.util.Map.Entry;

@GwtCompatible
final class FilteredEntrySetMultimap<K, V> extends FilteredEntryMultimap<K, V> implements FilteredSetMultimap<K, V> {
   FilteredEntrySetMultimap(SetMultimap<K, V> var1, Predicate<? super Entry<K, V>> var2) {
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

   Set<Entry<K, V>> createEntries() {
      return Sets.filter(this.unfiltered().entries(), this.entryPredicate());
   }

   public Set<Entry<K, V>> entries() {
      return (Set)super.entries();
   }
}
