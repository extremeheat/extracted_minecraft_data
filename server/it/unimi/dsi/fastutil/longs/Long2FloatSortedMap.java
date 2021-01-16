package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Long2FloatSortedMap extends Long2FloatMap, SortedMap<Long, Float> {
   Long2FloatSortedMap subMap(long var1, long var3);

   Long2FloatSortedMap headMap(long var1);

   Long2FloatSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   /** @deprecated */
   @Deprecated
   default Long2FloatSortedMap subMap(Long var1, Long var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long2FloatSortedMap headMap(Long var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long2FloatSortedMap tailMap(Long var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Long, Float>> entrySet() {
      return this.long2FloatEntrySet();
   }

   ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet();

   LongSortedSet keySet();

   FloatCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2FloatMap.Entry>, Long2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2FloatMap.Entry> fastIterator(Long2FloatMap.Entry var1);
   }
}
