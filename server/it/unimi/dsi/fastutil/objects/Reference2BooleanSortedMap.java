package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import java.util.Comparator;
import java.util.SortedMap;

public interface Reference2BooleanSortedMap<K> extends Reference2BooleanMap<K>, SortedMap<K, Boolean> {
   Reference2BooleanSortedMap<K> subMap(K var1, K var2);

   Reference2BooleanSortedMap<K> headMap(K var1);

   Reference2BooleanSortedMap<K> tailMap(K var1);

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<K, Boolean>> entrySet() {
      return this.reference2BooleanEntrySet();
   }

   ObjectSortedSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet();

   ReferenceSortedSet<K> keySet();

   BooleanCollection values();

   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Reference2BooleanMap.Entry<K>>, Reference2BooleanMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> fastIterator(Reference2BooleanMap.Entry<K> var1);
   }
}
