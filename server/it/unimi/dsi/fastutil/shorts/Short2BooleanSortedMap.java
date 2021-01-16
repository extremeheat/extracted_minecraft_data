package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Short2BooleanSortedMap extends Short2BooleanMap, SortedMap<Short, Boolean> {
   Short2BooleanSortedMap subMap(short var1, short var2);

   Short2BooleanSortedMap headMap(short var1);

   Short2BooleanSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   /** @deprecated */
   @Deprecated
   default Short2BooleanSortedMap subMap(Short var1, Short var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short2BooleanSortedMap headMap(Short var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short2BooleanSortedMap tailMap(Short var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Short, Boolean>> entrySet() {
      return this.short2BooleanEntrySet();
   }

   ObjectSortedSet<Short2BooleanMap.Entry> short2BooleanEntrySet();

   ShortSortedSet keySet();

   BooleanCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2BooleanMap.Entry>, Short2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2BooleanMap.Entry> fastIterator(Short2BooleanMap.Entry var1);
   }
}
