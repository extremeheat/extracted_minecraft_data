package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
abstract class AbstractSortedKeySortedSetMultimap<K, V> extends AbstractSortedSetMultimap<K, V> {
   AbstractSortedKeySortedSetMultimap(SortedMap<K, Collection<V>> var1) {
      super(var1);
   }

   public SortedMap<K, Collection<V>> asMap() {
      return (SortedMap)super.asMap();
   }

   SortedMap<K, Collection<V>> backingMap() {
      return (SortedMap)super.backingMap();
   }

   public SortedSet<K> keySet() {
      return (SortedSet)super.keySet();
   }
}
