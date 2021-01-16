package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Double2FloatSortedMap extends Double2FloatMap, SortedMap<Double, Float> {
   Double2FloatSortedMap subMap(double var1, double var3);

   Double2FloatSortedMap headMap(double var1);

   Double2FloatSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   /** @deprecated */
   @Deprecated
   default Double2FloatSortedMap subMap(Double var1, Double var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double2FloatSortedMap headMap(Double var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double2FloatSortedMap tailMap(Double var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Double, Float>> entrySet() {
      return this.double2FloatEntrySet();
   }

   ObjectSortedSet<Double2FloatMap.Entry> double2FloatEntrySet();

   DoubleSortedSet keySet();

   FloatCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2FloatMap.Entry>, Double2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2FloatMap.Entry> fastIterator(Double2FloatMap.Entry var1);
   }
}
