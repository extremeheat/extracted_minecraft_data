package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Double2CharSortedMap extends Double2CharMap, SortedMap<Double, Character> {
   Double2CharSortedMap subMap(double var1, double var3);

   Double2CharSortedMap headMap(double var1);

   Double2CharSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   /** @deprecated */
   @Deprecated
   default Double2CharSortedMap subMap(Double var1, Double var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double2CharSortedMap headMap(Double var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double2CharSortedMap tailMap(Double var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Double, Character>> entrySet() {
      return this.double2CharEntrySet();
   }

   ObjectSortedSet<Double2CharMap.Entry> double2CharEntrySet();

   DoubleSortedSet keySet();

   CharCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2CharMap.Entry>, Double2CharMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2CharMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2CharMap.Entry> fastIterator(Double2CharMap.Entry var1);
   }
}
