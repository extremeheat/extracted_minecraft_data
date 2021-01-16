package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Int2ObjectSortedMap<V> extends Int2ObjectMap<V>, SortedMap<Integer, V> {
   Int2ObjectSortedMap<V> subMap(int var1, int var2);

   Int2ObjectSortedMap<V> headMap(int var1);

   Int2ObjectSortedMap<V> tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   /** @deprecated */
   @Deprecated
   default Int2ObjectSortedMap<V> subMap(Integer var1, Integer var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Int2ObjectSortedMap<V> headMap(Integer var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Int2ObjectSortedMap<V> tailMap(Integer var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer firstKey() {
      return this.firstIntKey();
   }

   /** @deprecated */
   @Deprecated
   default Integer lastKey() {
      return this.lastIntKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Integer, V>> entrySet() {
      return this.int2ObjectEntrySet();
   }

   ObjectSortedSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet();

   IntSortedSet keySet();

   ObjectCollection<V> values();

   IntComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Int2ObjectMap.Entry<V>>, Int2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> fastIterator(Int2ObjectMap.Entry<V> var1);
   }
}
