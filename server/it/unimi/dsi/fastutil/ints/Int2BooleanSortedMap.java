package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Int2BooleanSortedMap extends Int2BooleanMap, SortedMap<Integer, Boolean> {
   Int2BooleanSortedMap subMap(int var1, int var2);

   Int2BooleanSortedMap headMap(int var1);

   Int2BooleanSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   /** @deprecated */
   @Deprecated
   default Int2BooleanSortedMap subMap(Integer var1, Integer var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Int2BooleanSortedMap headMap(Integer var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Int2BooleanSortedMap tailMap(Integer var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Integer, Boolean>> entrySet() {
      return this.int2BooleanEntrySet();
   }

   ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet();

   IntSortedSet keySet();

   BooleanCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2BooleanMap.Entry>, Int2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2BooleanMap.Entry> fastIterator(Int2BooleanMap.Entry var1);
   }
}
