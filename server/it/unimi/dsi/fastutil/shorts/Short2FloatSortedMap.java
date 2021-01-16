package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Short2FloatSortedMap extends Short2FloatMap, SortedMap<Short, Float> {
   Short2FloatSortedMap subMap(short var1, short var2);

   Short2FloatSortedMap headMap(short var1);

   Short2FloatSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   /** @deprecated */
   @Deprecated
   default Short2FloatSortedMap subMap(Short var1, Short var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short2FloatSortedMap headMap(Short var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short2FloatSortedMap tailMap(Short var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Short, Float>> entrySet() {
      return this.short2FloatEntrySet();
   }

   ObjectSortedSet<Short2FloatMap.Entry> short2FloatEntrySet();

   ShortSortedSet keySet();

   FloatCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2FloatMap.Entry>, Short2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2FloatMap.Entry> fastIterator(Short2FloatMap.Entry var1);
   }
}
