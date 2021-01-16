package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;

public interface Float2ReferenceSortedMap<V> extends Float2ReferenceMap<V>, SortedMap<Float, V> {
   Float2ReferenceSortedMap<V> subMap(float var1, float var2);

   Float2ReferenceSortedMap<V> headMap(float var1);

   Float2ReferenceSortedMap<V> tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   /** @deprecated */
   @Deprecated
   default Float2ReferenceSortedMap<V> subMap(Float var1, Float var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float2ReferenceSortedMap<V> headMap(Float var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float2ReferenceSortedMap<V> tailMap(Float var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float firstKey() {
      return this.firstFloatKey();
   }

   /** @deprecated */
   @Deprecated
   default Float lastKey() {
      return this.lastFloatKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Float, V>> entrySet() {
      return this.float2ReferenceEntrySet();
   }

   ObjectSortedSet<Float2ReferenceMap.Entry<V>> float2ReferenceEntrySet();

   FloatSortedSet keySet();

   ReferenceCollection<V> values();

   FloatComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Float2ReferenceMap.Entry<V>>, Float2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> fastIterator(Float2ReferenceMap.Entry<V> var1);
   }
}
