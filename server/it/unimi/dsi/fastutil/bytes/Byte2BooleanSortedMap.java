package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Byte2BooleanSortedMap extends Byte2BooleanMap, SortedMap<Byte, Boolean> {
   Byte2BooleanSortedMap subMap(byte var1, byte var2);

   Byte2BooleanSortedMap headMap(byte var1);

   Byte2BooleanSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   /** @deprecated */
   @Deprecated
   default Byte2BooleanSortedMap subMap(Byte var1, Byte var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte2BooleanSortedMap headMap(Byte var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte2BooleanSortedMap tailMap(Byte var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Byte, Boolean>> entrySet() {
      return this.byte2BooleanEntrySet();
   }

   ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet();

   ByteSortedSet keySet();

   BooleanCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2BooleanMap.Entry>, Byte2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2BooleanMap.Entry> fastIterator(Byte2BooleanMap.Entry var1);
   }
}
