package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Long2ByteSortedMap extends Long2ByteMap, SortedMap<Long, Byte> {
   Long2ByteSortedMap subMap(long var1, long var3);

   Long2ByteSortedMap headMap(long var1);

   Long2ByteSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   /** @deprecated */
   @Deprecated
   default Long2ByteSortedMap subMap(Long var1, Long var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long2ByteSortedMap headMap(Long var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long2ByteSortedMap tailMap(Long var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long firstKey() {
      return this.firstLongKey();
   }

   /** @deprecated */
   @Deprecated
   default Long lastKey() {
      return this.lastLongKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Long, Byte>> entrySet() {
      return this.long2ByteEntrySet();
   }

   ObjectSortedSet<Long2ByteMap.Entry> long2ByteEntrySet();

   LongSortedSet keySet();

   ByteCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2ByteMap.Entry>, Long2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2ByteMap.Entry> fastIterator(Long2ByteMap.Entry var1);
   }
}
