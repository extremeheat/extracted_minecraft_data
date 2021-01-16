package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Long2BooleanSortedMap extends Long2BooleanMap, SortedMap<Long, Boolean> {
   Long2BooleanSortedMap subMap(long var1, long var3);

   Long2BooleanSortedMap headMap(long var1);

   Long2BooleanSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   /** @deprecated */
   @Deprecated
   default Long2BooleanSortedMap subMap(Long var1, Long var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long2BooleanSortedMap headMap(Long var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long2BooleanSortedMap tailMap(Long var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Long, Boolean>> entrySet() {
      return this.long2BooleanEntrySet();
   }

   ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet();

   LongSortedSet keySet();

   BooleanCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2BooleanMap.Entry>, Long2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2BooleanMap.Entry> fastIterator(Long2BooleanMap.Entry var1);
   }
}
