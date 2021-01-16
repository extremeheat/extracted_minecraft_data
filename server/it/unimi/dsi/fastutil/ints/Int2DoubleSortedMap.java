package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Int2DoubleSortedMap extends Int2DoubleMap, SortedMap<Integer, Double> {
   Int2DoubleSortedMap subMap(int var1, int var2);

   Int2DoubleSortedMap headMap(int var1);

   Int2DoubleSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   /** @deprecated */
   @Deprecated
   default Int2DoubleSortedMap subMap(Integer var1, Integer var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Int2DoubleSortedMap headMap(Integer var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Int2DoubleSortedMap tailMap(Integer var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Integer, Double>> entrySet() {
      return this.int2DoubleEntrySet();
   }

   ObjectSortedSet<Int2DoubleMap.Entry> int2DoubleEntrySet();

   IntSortedSet keySet();

   DoubleCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2DoubleMap.Entry>, Int2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2DoubleMap.Entry> fastIterator(Int2DoubleMap.Entry var1);
   }
}
