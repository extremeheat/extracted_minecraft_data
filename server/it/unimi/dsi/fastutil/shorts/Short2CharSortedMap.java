package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Short2CharSortedMap extends Short2CharMap, SortedMap<Short, Character> {
   Short2CharSortedMap subMap(short var1, short var2);

   Short2CharSortedMap headMap(short var1);

   Short2CharSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   /** @deprecated */
   @Deprecated
   default Short2CharSortedMap subMap(Short var1, Short var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short2CharSortedMap headMap(Short var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short2CharSortedMap tailMap(Short var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Short, Character>> entrySet() {
      return this.short2CharEntrySet();
   }

   ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet();

   ShortSortedSet keySet();

   CharCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2CharMap.Entry>, Short2CharMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2CharMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2CharMap.Entry> fastIterator(Short2CharMap.Entry var1);
   }
}
