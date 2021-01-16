package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.LongCollection;
import java.util.Comparator;
import java.util.SortedMap;

public interface Object2LongSortedMap<K> extends Object2LongMap<K>, SortedMap<K, Long> {
   Object2LongSortedMap<K> subMap(K var1, K var2);

   Object2LongSortedMap<K> headMap(K var1);

   Object2LongSortedMap<K> tailMap(K var1);

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<K, Long>> entrySet() {
      return this.object2LongEntrySet();
   }

   ObjectSortedSet<Object2LongMap.Entry<K>> object2LongEntrySet();

   ObjectSortedSet<K> keySet();

   LongCollection values();

   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Object2LongMap.Entry<K>>, Object2LongMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Object2LongMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Object2LongMap.Entry<K>> fastIterator(Object2LongMap.Entry<K> var1);
   }
}
