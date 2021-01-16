package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Int2IntSortedMap extends Int2IntMap, SortedMap<Integer, Integer> {
   Int2IntSortedMap subMap(int var1, int var2);

   Int2IntSortedMap headMap(int var1);

   Int2IntSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   /** @deprecated */
   @Deprecated
   default Int2IntSortedMap subMap(Integer var1, Integer var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Int2IntSortedMap headMap(Integer var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Int2IntSortedMap tailMap(Integer var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Integer, Integer>> entrySet() {
      return this.int2IntEntrySet();
   }

   ObjectSortedSet<Int2IntMap.Entry> int2IntEntrySet();

   IntSortedSet keySet();

   IntCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2IntMap.Entry>, Int2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2IntMap.Entry> fastIterator(Int2IntMap.Entry var1);
   }
}
