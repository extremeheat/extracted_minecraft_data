package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.IntCollection;
import java.util.Comparator;
import java.util.SortedMap;

public interface Object2IntSortedMap<K> extends Object2IntMap<K>, SortedMap<K, Integer> {
   Object2IntSortedMap<K> subMap(K var1, K var2);

   Object2IntSortedMap<K> headMap(K var1);

   Object2IntSortedMap<K> tailMap(K var1);

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<K, Integer>> entrySet() {
      return this.object2IntEntrySet();
   }

   ObjectSortedSet<Object2IntMap.Entry<K>> object2IntEntrySet();

   ObjectSortedSet<K> keySet();

   IntCollection values();

   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Object2IntMap.Entry<K>>, Object2IntMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Object2IntMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Object2IntMap.Entry<K>> fastIterator(Object2IntMap.Entry<K> var1);
   }
}
