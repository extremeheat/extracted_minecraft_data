package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Short2LongSortedMap extends Short2LongMap, SortedMap<Short, Long> {
   Short2LongSortedMap subMap(short var1, short var2);

   Short2LongSortedMap headMap(short var1);

   Short2LongSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   /** @deprecated */
   @Deprecated
   default Short2LongSortedMap subMap(Short var1, Short var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short2LongSortedMap headMap(Short var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short2LongSortedMap tailMap(Short var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Short, Long>> entrySet() {
      return this.short2LongEntrySet();
   }

   ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet();

   ShortSortedSet keySet();

   LongCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2LongMap.Entry>, Short2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2LongMap.Entry> fastIterator(Short2LongMap.Entry var1);
   }
}
