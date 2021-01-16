package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.SortedMap;

public interface Int2ShortSortedMap extends Int2ShortMap, SortedMap<Integer, Short> {
   Int2ShortSortedMap subMap(int var1, int var2);

   Int2ShortSortedMap headMap(int var1);

   Int2ShortSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   /** @deprecated */
   @Deprecated
   default Int2ShortSortedMap subMap(Integer var1, Integer var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Int2ShortSortedMap headMap(Integer var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Int2ShortSortedMap tailMap(Integer var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Integer, Short>> entrySet() {
      return this.int2ShortEntrySet();
   }

   ObjectSortedSet<Int2ShortMap.Entry> int2ShortEntrySet();

   IntSortedSet keySet();

   ShortCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2ShortMap.Entry>, Int2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2ShortMap.Entry> fastIterator(Int2ShortMap.Entry var1);
   }
}
