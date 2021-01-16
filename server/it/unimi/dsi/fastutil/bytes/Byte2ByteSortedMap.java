package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Byte2ByteSortedMap extends Byte2ByteMap, SortedMap<Byte, Byte> {
   Byte2ByteSortedMap subMap(byte var1, byte var2);

   Byte2ByteSortedMap headMap(byte var1);

   Byte2ByteSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   /** @deprecated */
   @Deprecated
   default Byte2ByteSortedMap subMap(Byte var1, Byte var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte2ByteSortedMap headMap(Byte var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte2ByteSortedMap tailMap(Byte var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Byte, Byte>> entrySet() {
      return this.byte2ByteEntrySet();
   }

   ObjectSortedSet<Byte2ByteMap.Entry> byte2ByteEntrySet();

   ByteSortedSet keySet();

   ByteCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2ByteMap.Entry>, Byte2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2ByteMap.Entry> fastIterator(Byte2ByteMap.Entry var1);
   }
}
