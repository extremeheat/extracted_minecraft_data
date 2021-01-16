package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import java.util.Comparator;
import java.util.SortedMap;

public interface Object2FloatSortedMap<K> extends Object2FloatMap<K>, SortedMap<K, Float> {
   Object2FloatSortedMap<K> subMap(K var1, K var2);

   Object2FloatSortedMap<K> headMap(K var1);

   Object2FloatSortedMap<K> tailMap(K var1);

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<K, Float>> entrySet() {
      return this.object2FloatEntrySet();
   }

   ObjectSortedSet<Object2FloatMap.Entry<K>> object2FloatEntrySet();

   ObjectSortedSet<K> keySet();

   FloatCollection values();

   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Object2FloatMap.Entry<K>>, Object2FloatMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> fastIterator(Object2FloatMap.Entry<K> var1);
   }
}
