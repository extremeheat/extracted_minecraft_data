package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Double2BooleanSortedMap extends Double2BooleanMap, SortedMap<Double, Boolean> {
   Double2BooleanSortedMap subMap(double var1, double var3);

   Double2BooleanSortedMap headMap(double var1);

   Double2BooleanSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   /** @deprecated */
   @Deprecated
   default Double2BooleanSortedMap subMap(Double var1, Double var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double2BooleanSortedMap headMap(Double var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double2BooleanSortedMap tailMap(Double var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Double, Boolean>> entrySet() {
      return this.double2BooleanEntrySet();
   }

   ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet();

   DoubleSortedSet keySet();

   BooleanCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2BooleanMap.Entry>, Double2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2BooleanMap.Entry> fastIterator(Double2BooleanMap.Entry var1);
   }
}
