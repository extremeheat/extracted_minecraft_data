package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Double2ByteSortedMap extends Double2ByteMap, SortedMap<Double, Byte> {
   Double2ByteSortedMap subMap(double var1, double var3);

   Double2ByteSortedMap headMap(double var1);

   Double2ByteSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   /** @deprecated */
   @Deprecated
   default Double2ByteSortedMap subMap(Double var1, Double var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double2ByteSortedMap headMap(Double var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double2ByteSortedMap tailMap(Double var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Double, Byte>> entrySet() {
      return this.double2ByteEntrySet();
   }

   ObjectSortedSet<Double2ByteMap.Entry> double2ByteEntrySet();

   DoubleSortedSet keySet();

   ByteCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2ByteMap.Entry>, Double2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2ByteMap.Entry> fastIterator(Double2ByteMap.Entry var1);
   }
}
