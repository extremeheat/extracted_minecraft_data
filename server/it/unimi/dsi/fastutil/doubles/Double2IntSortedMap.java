package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Double2IntSortedMap extends Double2IntMap, SortedMap<Double, Integer> {
   Double2IntSortedMap subMap(double var1, double var3);

   Double2IntSortedMap headMap(double var1);

   Double2IntSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   /** @deprecated */
   @Deprecated
   default Double2IntSortedMap subMap(Double var1, Double var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double2IntSortedMap headMap(Double var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double2IntSortedMap tailMap(Double var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Double, Integer>> entrySet() {
      return this.double2IntEntrySet();
   }

   ObjectSortedSet<Double2IntMap.Entry> double2IntEntrySet();

   DoubleSortedSet keySet();

   IntCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2IntMap.Entry>, Double2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2IntMap.Entry> fastIterator(Double2IntMap.Entry var1);
   }
}
