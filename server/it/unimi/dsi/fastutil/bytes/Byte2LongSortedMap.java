package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Byte2LongSortedMap extends Byte2LongMap, SortedMap<Byte, Long> {
   Byte2LongSortedMap subMap(byte var1, byte var2);

   Byte2LongSortedMap headMap(byte var1);

   Byte2LongSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   /** @deprecated */
   @Deprecated
   default Byte2LongSortedMap subMap(Byte var1, Byte var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte2LongSortedMap headMap(Byte var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte2LongSortedMap tailMap(Byte var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Byte, Long>> entrySet() {
      return this.byte2LongEntrySet();
   }

   ObjectSortedSet<Byte2LongMap.Entry> byte2LongEntrySet();

   ByteSortedSet keySet();

   LongCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2LongMap.Entry>, Byte2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2LongMap.Entry> fastIterator(Byte2LongMap.Entry var1);
   }
}
