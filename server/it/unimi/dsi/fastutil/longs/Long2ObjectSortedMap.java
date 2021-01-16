package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Long2ObjectSortedMap<V> extends Long2ObjectMap<V>, SortedMap<Long, V> {
   Long2ObjectSortedMap<V> subMap(long var1, long var3);

   Long2ObjectSortedMap<V> headMap(long var1);

   Long2ObjectSortedMap<V> tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   /** @deprecated */
   @Deprecated
   default Long2ObjectSortedMap<V> subMap(Long var1, Long var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long2ObjectSortedMap<V> headMap(Long var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long2ObjectSortedMap<V> tailMap(Long var1) {
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
      return this.long2ObjectEntrySet();
   }

   ObjectSortedSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet();

   LongSortedSet keySet();

   ObjectCollection<V> values();

   LongComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Long2ObjectMap.Entry<V>>, Long2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> fastIterator(Long2ObjectMap.Entry<V> var1);
   }
}
