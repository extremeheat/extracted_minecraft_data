package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Byte2DoubleSortedMap extends Byte2DoubleMap, SortedMap<Byte, Double> {
   Byte2DoubleSortedMap subMap(byte var1, byte var2);

   Byte2DoubleSortedMap headMap(byte var1);

   Byte2DoubleSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   /** @deprecated */
   @Deprecated
   default Byte2DoubleSortedMap subMap(Byte var1, Byte var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte2DoubleSortedMap headMap(Byte var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte2DoubleSortedMap tailMap(Byte var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte firstKey() {
      return this.firstByteKey();
   }

   /** @deprecated */
   @Deprecated
   default Byte lastKey() {
      return this.lastByteKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Byte, Double>> entrySet() {
      return this.byte2DoubleEntrySet();
   }

   ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet();

   ByteSortedSet keySet();

   DoubleCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2DoubleMap.Entry>, Byte2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2DoubleMap.Entry> fastIterator(Byte2DoubleMap.Entry var1);
   }
}
