package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Short2ShortSortedMap extends Short2ShortMap, SortedMap<Short, Short> {
   Short2ShortSortedMap subMap(short var1, short var2);

   Short2ShortSortedMap headMap(short var1);

   Short2ShortSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   /** @deprecated */
   @Deprecated
   default Short2ShortSortedMap subMap(Short var1, Short var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short2ShortSortedMap headMap(Short var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short2ShortSortedMap tailMap(Short var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Short, Short>> entrySet() {
      return this.short2ShortEntrySet();
   }

   ObjectSortedSet<Short2ShortMap.Entry> short2ShortEntrySet();

   ShortSortedSet keySet();

   ShortCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2ShortMap.Entry>, Short2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2ShortMap.Entry> fastIterator(Short2ShortMap.Entry var1);
   }
}
