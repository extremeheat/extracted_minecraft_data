package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Double2ObjectSortedMap<V> extends Double2ObjectMap<V>, SortedMap<Double, V> {
   Double2ObjectSortedMap<V> subMap(double var1, double var3);

   Double2ObjectSortedMap<V> headMap(double var1);

   Double2ObjectSortedMap<V> tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   /** @deprecated */
   @Deprecated
   default Double2ObjectSortedMap<V> subMap(Double var1, Double var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double2ObjectSortedMap<V> headMap(Double var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double2ObjectSortedMap<V> tailMap(Double var1) {
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
      return this.double2ObjectEntrySet();
   }

   ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet();

   DoubleSortedSet keySet();

   ObjectCollection<V> values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Double2ObjectMap.Entry<V>>, Double2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> fastIterator(Double2ObjectMap.Entry<V> var1);
   }
}
