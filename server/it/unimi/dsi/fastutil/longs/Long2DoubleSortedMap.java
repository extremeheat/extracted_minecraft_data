package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Long2DoubleSortedMap extends Long2DoubleMap, SortedMap<Long, Double> {
   Long2DoubleSortedMap subMap(long var1, long var3);

   Long2DoubleSortedMap headMap(long var1);

   Long2DoubleSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   /** @deprecated */
   @Deprecated
   default Long2DoubleSortedMap subMap(Long var1, Long var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long2DoubleSortedMap headMap(Long var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long2DoubleSortedMap tailMap(Long var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Long, Double>> entrySet() {
      return this.long2DoubleEntrySet();
   }

   ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet();

   LongSortedSet keySet();

   DoubleCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2DoubleMap.Entry>, Long2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2DoubleMap.Entry> fastIterator(Long2DoubleMap.Entry var1);
   }
}
