package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Double2DoubleSortedMap extends Double2DoubleMap, SortedMap<Double, Double> {
   Double2DoubleSortedMap subMap(double var1, double var3);

   Double2DoubleSortedMap headMap(double var1);

   Double2DoubleSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   /** @deprecated */
   @Deprecated
   default Double2DoubleSortedMap subMap(Double var1, Double var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double2DoubleSortedMap headMap(Double var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double2DoubleSortedMap tailMap(Double var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Double, Double>> entrySet() {
      return this.double2DoubleEntrySet();
   }

   ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet();

   DoubleSortedSet keySet();

   DoubleCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2DoubleMap.Entry>, Double2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2DoubleMap.Entry> fastIterator(Double2DoubleMap.Entry var1);
   }
}
