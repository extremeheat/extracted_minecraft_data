package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;

public interface Double2ReferenceSortedMap<V> extends Double2ReferenceMap<V>, SortedMap<Double, V> {
   Double2ReferenceSortedMap<V> subMap(double var1, double var3);

   Double2ReferenceSortedMap<V> headMap(double var1);

   Double2ReferenceSortedMap<V> tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   /** @deprecated */
   @Deprecated
   default Double2ReferenceSortedMap<V> subMap(Double var1, Double var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double2ReferenceSortedMap<V> headMap(Double var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double2ReferenceSortedMap<V> tailMap(Double var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Double, V>> entrySet() {
      return this.double2ReferenceEntrySet();
   }

   ObjectSortedSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet();

   DoubleSortedSet keySet();

   ReferenceCollection<V> values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Double2ReferenceMap.Entry<V>>, Double2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> fastIterator(Double2ReferenceMap.Entry<V> var1);
   }
}
