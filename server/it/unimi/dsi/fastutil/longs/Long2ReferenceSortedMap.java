package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;

public interface Long2ReferenceSortedMap<V> extends Long2ReferenceMap<V>, SortedMap<Long, V> {
   Long2ReferenceSortedMap<V> subMap(long var1, long var3);

   Long2ReferenceSortedMap<V> headMap(long var1);

   Long2ReferenceSortedMap<V> tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   /** @deprecated */
   @Deprecated
   default Long2ReferenceSortedMap<V> subMap(Long var1, Long var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long2ReferenceSortedMap<V> headMap(Long var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long2ReferenceSortedMap<V> tailMap(Long var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long firstKey() {
      return this.firstLongKey();
   }

   /** @deprecated */
   @Deprecated
   default Long lastKey() {
      return this.lastLongKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Long, V>> entrySet() {
      return this.long2ReferenceEntrySet();
   }

   ObjectSortedSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet();

   LongSortedSet keySet();

   ReferenceCollection<V> values();

   LongComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Long2ReferenceMap.Entry<V>>, Long2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> fastIterator(Long2ReferenceMap.Entry<V> var1);
   }
}
