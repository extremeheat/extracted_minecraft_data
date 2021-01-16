package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Double2LongSortedMap extends Double2LongMap, SortedMap<Double, Long> {
   Double2LongSortedMap subMap(double var1, double var3);

   Double2LongSortedMap headMap(double var1);

   Double2LongSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   /** @deprecated */
   @Deprecated
   default Double2LongSortedMap subMap(Double var1, Double var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double2LongSortedMap headMap(Double var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double2LongSortedMap tailMap(Double var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double firstKey() {
      return this.firstDoubleKey();
   }

   /** @deprecated */
   @Deprecated
   default Double lastKey() {
      return this.lastDoubleKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Double, Long>> entrySet() {
      return this.double2LongEntrySet();
   }

   ObjectSortedSet<Double2LongMap.Entry> double2LongEntrySet();

   DoubleSortedSet keySet();

   LongCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2LongMap.Entry>, Double2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2LongMap.Entry> fastIterator(Double2LongMap.Entry var1);
   }
}
