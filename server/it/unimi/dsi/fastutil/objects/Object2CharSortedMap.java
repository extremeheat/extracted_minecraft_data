package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.CharCollection;
import java.util.Comparator;
import java.util.SortedMap;

public interface Object2CharSortedMap<K> extends Object2CharMap<K>, SortedMap<K, Character> {
   Object2CharSortedMap<K> subMap(K var1, K var2);

   Object2CharSortedMap<K> headMap(K var1);

   Object2CharSortedMap<K> tailMap(K var1);

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<K, Character>> entrySet() {
      return this.object2CharEntrySet();
   }

   ObjectSortedSet<Object2CharMap.Entry<K>> object2CharEntrySet();

   ObjectSortedSet<K> keySet();

   CharCollection values();

   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Object2CharMap.Entry<K>>, Object2CharMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Object2CharMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Object2CharMap.Entry<K>> fastIterator(Object2CharMap.Entry<K> var1);
   }
}
