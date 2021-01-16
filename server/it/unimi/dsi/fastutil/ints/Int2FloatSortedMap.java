package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Int2FloatSortedMap extends Int2FloatMap, SortedMap<Integer, Float> {
   Int2FloatSortedMap subMap(int var1, int var2);

   Int2FloatSortedMap headMap(int var1);

   Int2FloatSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   /** @deprecated */
   @Deprecated
   default Int2FloatSortedMap subMap(Integer var1, Integer var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Int2FloatSortedMap headMap(Integer var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Int2FloatSortedMap tailMap(Integer var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Integer, Float>> entrySet() {
      return this.int2FloatEntrySet();
   }

   ObjectSortedSet<Int2FloatMap.Entry> int2FloatEntrySet();

   IntSortedSet keySet();

   FloatCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2FloatMap.Entry>, Int2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2FloatMap.Entry> fastIterator(Int2FloatMap.Entry var1);
   }
}
