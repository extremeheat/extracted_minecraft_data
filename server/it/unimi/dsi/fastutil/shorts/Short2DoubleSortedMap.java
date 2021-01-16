package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Short2DoubleSortedMap extends Short2DoubleMap, SortedMap<Short, Double> {
   Short2DoubleSortedMap subMap(short var1, short var2);

   Short2DoubleSortedMap headMap(short var1);

   Short2DoubleSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   /** @deprecated */
   @Deprecated
   default Short2DoubleSortedMap subMap(Short var1, Short var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short2DoubleSortedMap headMap(Short var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short2DoubleSortedMap tailMap(Short var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short firstKey() {
      return this.firstShortKey();
   }

   /** @deprecated */
   @Deprecated
   default Short lastKey() {
      return this.lastShortKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Short, Double>> entrySet() {
      return this.short2DoubleEntrySet();
   }

   ObjectSortedSet<Short2DoubleMap.Entry> short2DoubleEntrySet();

   ShortSortedSet keySet();

   DoubleCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2DoubleMap.Entry>, Short2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2DoubleMap.Entry> fastIterator(Short2DoubleMap.Entry var1);
   }
}
