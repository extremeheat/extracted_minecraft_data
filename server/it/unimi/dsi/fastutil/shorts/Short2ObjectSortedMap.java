package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Short2ObjectSortedMap<V> extends Short2ObjectMap<V>, SortedMap<Short, V> {
   Short2ObjectSortedMap<V> subMap(short var1, short var2);

   Short2ObjectSortedMap<V> headMap(short var1);

   Short2ObjectSortedMap<V> tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   /** @deprecated */
   @Deprecated
   default Short2ObjectSortedMap<V> subMap(Short var1, Short var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short2ObjectSortedMap<V> headMap(Short var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short2ObjectSortedMap<V> tailMap(Short var1) {
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
      return this.short2ObjectEntrySet();
   }

   ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet();

   ShortSortedSet keySet();

   ObjectCollection<V> values();

   ShortComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Short2ObjectMap.Entry<V>>, Short2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> fastIterator(Short2ObjectMap.Entry<V> var1);
   }
}
