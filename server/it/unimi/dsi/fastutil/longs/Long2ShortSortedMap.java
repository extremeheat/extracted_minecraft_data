package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.SortedMap;

public interface Long2ShortSortedMap extends Long2ShortMap, SortedMap<Long, Short> {
   Long2ShortSortedMap subMap(long var1, long var3);

   Long2ShortSortedMap headMap(long var1);

   Long2ShortSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   /** @deprecated */
   @Deprecated
   default Long2ShortSortedMap subMap(Long var1, Long var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long2ShortSortedMap headMap(Long var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long2ShortSortedMap tailMap(Long var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Long, Short>> entrySet() {
      return this.long2ShortEntrySet();
   }

   ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet();

   LongSortedSet keySet();

   ShortCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2ShortMap.Entry>, Long2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2ShortMap.Entry> fastIterator(Long2ShortMap.Entry var1);
   }
}
