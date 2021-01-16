package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;

public interface Short2ReferenceSortedMap<V> extends Short2ReferenceMap<V>, SortedMap<Short, V> {
   Short2ReferenceSortedMap<V> subMap(short var1, short var2);

   Short2ReferenceSortedMap<V> headMap(short var1);

   Short2ReferenceSortedMap<V> tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   /** @deprecated */
   @Deprecated
   default Short2ReferenceSortedMap<V> subMap(Short var1, Short var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short2ReferenceSortedMap<V> headMap(Short var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short2ReferenceSortedMap<V> tailMap(Short var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Short, V>> entrySet() {
      return this.short2ReferenceEntrySet();
   }

   ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet();

   ShortSortedSet keySet();

   ReferenceCollection<V> values();

   ShortComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Short2ReferenceMap.Entry<V>>, Short2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> fastIterator(Short2ReferenceMap.Entry<V> var1);
   }
}
