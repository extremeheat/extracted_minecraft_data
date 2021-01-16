package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;
import java.util.SortedMap;

public interface Reference2ObjectSortedMap<K, V> extends Reference2ObjectMap<K, V>, SortedMap<K, V> {
   Reference2ObjectSortedMap<K, V> subMap(K var1, K var2);

   Reference2ObjectSortedMap<K, V> headMap(K var1);

   Reference2ObjectSortedMap<K, V> tailMap(K var1);

   default ObjectSortedSet<java.util.Map.Entry<K, V>> entrySet() {
      return this.reference2ObjectEntrySet();
   }

   ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet();

   ReferenceSortedSet<K> keySet();

   ObjectCollection<V> values();

   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K, V> extends ObjectSortedSet<Reference2ObjectMap.Entry<K, V>>, Reference2ObjectMap.FastEntrySet<K, V> {
      ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> fastIterator();

      ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> fastIterator(Reference2ObjectMap.Entry<K, V> var1);
   }
}
