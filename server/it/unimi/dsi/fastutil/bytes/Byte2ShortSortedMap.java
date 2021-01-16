package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.SortedMap;

public interface Byte2ShortSortedMap extends Byte2ShortMap, SortedMap<Byte, Short> {
   Byte2ShortSortedMap subMap(byte var1, byte var2);

   Byte2ShortSortedMap headMap(byte var1);

   Byte2ShortSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   /** @deprecated */
   @Deprecated
   default Byte2ShortSortedMap subMap(Byte var1, Byte var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte2ShortSortedMap headMap(Byte var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte2ShortSortedMap tailMap(Byte var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Byte, Short>> entrySet() {
      return this.byte2ShortEntrySet();
   }

   ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet();

   ByteSortedSet keySet();

   ShortCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2ShortMap.Entry>, Byte2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2ShortMap.Entry> fastIterator(Byte2ShortMap.Entry var1);
   }
}
