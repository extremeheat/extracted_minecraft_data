package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Long2LongSortedMap extends Long2LongMap, SortedMap<Long, Long> {
   Long2LongSortedMap subMap(long var1, long var3);

   Long2LongSortedMap headMap(long var1);

   Long2LongSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   /** @deprecated */
   @Deprecated
   default Long2LongSortedMap subMap(Long var1, Long var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long2LongSortedMap headMap(Long var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long2LongSortedMap tailMap(Long var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Long, Long>> entrySet() {
      return this.long2LongEntrySet();
   }

   ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet();

   LongSortedSet keySet();

   LongCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2LongMap.Entry>, Long2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2LongMap.Entry> fastIterator(Long2LongMap.Entry var1);
   }
}
