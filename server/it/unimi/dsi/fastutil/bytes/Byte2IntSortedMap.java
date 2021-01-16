package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Byte2IntSortedMap extends Byte2IntMap, SortedMap<Byte, Integer> {
   Byte2IntSortedMap subMap(byte var1, byte var2);

   Byte2IntSortedMap headMap(byte var1);

   Byte2IntSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   /** @deprecated */
   @Deprecated
   default Byte2IntSortedMap subMap(Byte var1, Byte var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte2IntSortedMap headMap(Byte var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte2IntSortedMap tailMap(Byte var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Byte, Integer>> entrySet() {
      return this.byte2IntEntrySet();
   }

   ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet();

   ByteSortedSet keySet();

   IntCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2IntMap.Entry>, Byte2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator(Byte2IntMap.Entry var1);
   }
}
