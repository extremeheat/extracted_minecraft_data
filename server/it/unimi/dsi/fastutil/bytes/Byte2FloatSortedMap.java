package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Byte2FloatSortedMap extends Byte2FloatMap, SortedMap<Byte, Float> {
   Byte2FloatSortedMap subMap(byte var1, byte var2);

   Byte2FloatSortedMap headMap(byte var1);

   Byte2FloatSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   /** @deprecated */
   @Deprecated
   default Byte2FloatSortedMap subMap(Byte var1, Byte var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte2FloatSortedMap headMap(Byte var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte2FloatSortedMap tailMap(Byte var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Byte, Float>> entrySet() {
      return this.byte2FloatEntrySet();
   }

   ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet();

   ByteSortedSet keySet();

   FloatCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2FloatMap.Entry>, Byte2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2FloatMap.Entry> fastIterator(Byte2FloatMap.Entry var1);
   }
}
