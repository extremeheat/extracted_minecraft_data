package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Int2LongSortedMap extends Int2LongMap, SortedMap<Integer, Long> {
   Int2LongSortedMap subMap(int var1, int var2);

   Int2LongSortedMap headMap(int var1);

   Int2LongSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   /** @deprecated */
   @Deprecated
   default Int2LongSortedMap subMap(Integer var1, Integer var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Int2LongSortedMap headMap(Integer var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Int2LongSortedMap tailMap(Integer var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer firstKey() {
      return this.firstIntKey();
   }

   /** @deprecated */
   @Deprecated
   default Integer lastKey() {
      return this.lastIntKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Integer, Long>> entrySet() {
      return this.int2LongEntrySet();
   }

   ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet();

   IntSortedSet keySet();

   LongCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2LongMap.Entry>, Int2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2LongMap.Entry> fastIterator(Int2LongMap.Entry var1);
   }
}
