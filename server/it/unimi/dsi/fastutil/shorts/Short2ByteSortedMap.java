package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Short2ByteSortedMap extends Short2ByteMap, SortedMap<Short, Byte> {
   Short2ByteSortedMap subMap(short var1, short var2);

   Short2ByteSortedMap headMap(short var1);

   Short2ByteSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   /** @deprecated */
   @Deprecated
   default Short2ByteSortedMap subMap(Short var1, Short var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short2ByteSortedMap headMap(Short var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short2ByteSortedMap tailMap(Short var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short firstKey() {
      return this.firstShortKey();
   }

   /** @deprecated */
   @Deprecated
   default Short lastKey() {
      return this.lastShortKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Short, Byte>> entrySet() {
      return this.short2ByteEntrySet();
   }

   ObjectSortedSet<Short2ByteMap.Entry> short2ByteEntrySet();

   ShortSortedSet keySet();

   ByteCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2ByteMap.Entry>, Short2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2ByteMap.Entry> fastIterator(Short2ByteMap.Entry var1);
   }
}
