package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Long2IntSortedMap extends Long2IntMap, SortedMap<Long, Integer> {
   Long2IntSortedMap subMap(long var1, long var3);

   Long2IntSortedMap headMap(long var1);

   Long2IntSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   /** @deprecated */
   @Deprecated
   default Long2IntSortedMap subMap(Long var1, Long var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long2IntSortedMap headMap(Long var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long2IntSortedMap tailMap(Long var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Long, Integer>> entrySet() {
      return this.long2IntEntrySet();
   }

   ObjectSortedSet<Long2IntMap.Entry> long2IntEntrySet();

   LongSortedSet keySet();

   IntCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2IntMap.Entry>, Long2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2IntMap.Entry> fastIterator(Long2IntMap.Entry var1);
   }
}
