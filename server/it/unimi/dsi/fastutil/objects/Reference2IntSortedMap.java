package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.IntCollection;
import java.util.Comparator;
import java.util.SortedMap;

public interface Reference2IntSortedMap<K> extends Reference2IntMap<K>, SortedMap<K, Integer> {
   Reference2IntSortedMap<K> subMap(K var1, K var2);

   Reference2IntSortedMap<K> headMap(K var1);

   Reference2IntSortedMap<K> tailMap(K var1);

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<K, Integer>> entrySet() {
      return this.reference2IntEntrySet();
   }

   ObjectSortedSet<Reference2IntMap.Entry<K>> reference2IntEntrySet();

   ReferenceSortedSet<K> keySet();

   IntCollection values();

   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Reference2IntMap.Entry<K>>, Reference2IntMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> fastIterator(Reference2IntMap.Entry<K> var1);
   }
}
