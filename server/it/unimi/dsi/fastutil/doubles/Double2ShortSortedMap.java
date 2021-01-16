package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.SortedMap;

public interface Double2ShortSortedMap extends Double2ShortMap, SortedMap<Double, Short> {
   Double2ShortSortedMap subMap(double var1, double var3);

   Double2ShortSortedMap headMap(double var1);

   Double2ShortSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   /** @deprecated */
   @Deprecated
   default Double2ShortSortedMap subMap(Double var1, Double var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double2ShortSortedMap headMap(Double var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double2ShortSortedMap tailMap(Double var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Double, Short>> entrySet() {
      return this.double2ShortEntrySet();
   }

   ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet();

   DoubleSortedSet keySet();

   ShortCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2ShortMap.Entry>, Double2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2ShortMap.Entry> fastIterator(Double2ShortMap.Entry var1);
   }
}
