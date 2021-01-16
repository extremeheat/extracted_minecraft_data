package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Short2IntSortedMap extends Short2IntMap, SortedMap<Short, Integer> {
   Short2IntSortedMap subMap(short var1, short var2);

   Short2IntSortedMap headMap(short var1);

   Short2IntSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   /** @deprecated */
   @Deprecated
   default Short2IntSortedMap subMap(Short var1, Short var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short2IntSortedMap headMap(Short var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short2IntSortedMap tailMap(Short var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Short, Integer>> entrySet() {
      return this.short2IntEntrySet();
   }

   ObjectSortedSet<Short2IntMap.Entry> short2IntEntrySet();

   ShortSortedSet keySet();

   IntCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2IntMap.Entry>, Short2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2IntMap.Entry> fastIterator(Short2IntMap.Entry var1);
   }
}
