package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;

public interface Int2ReferenceSortedMap<V> extends Int2ReferenceMap<V>, SortedMap<Integer, V> {
   Int2ReferenceSortedMap<V> subMap(int var1, int var2);

   Int2ReferenceSortedMap<V> headMap(int var1);

   Int2ReferenceSortedMap<V> tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   /** @deprecated */
   @Deprecated
   default Int2ReferenceSortedMap<V> subMap(Integer var1, Integer var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Int2ReferenceSortedMap<V> headMap(Integer var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Int2ReferenceSortedMap<V> tailMap(Integer var1) {
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
      return this.int2ReferenceEntrySet();
   }

   ObjectSortedSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet();

   IntSortedSet keySet();

   ReferenceCollection<V> values();

   IntComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Int2ReferenceMap.Entry<V>>, Int2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> fastIterator(Int2ReferenceMap.Entry<V> var1);
   }
}
