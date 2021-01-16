package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Float2ObjectSortedMap<V> extends Float2ObjectMap<V>, SortedMap<Float, V> {
   Float2ObjectSortedMap<V> subMap(float var1, float var2);

   Float2ObjectSortedMap<V> headMap(float var1);

   Float2ObjectSortedMap<V> tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   /** @deprecated */
   @Deprecated
   default Float2ObjectSortedMap<V> subMap(Float var1, Float var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float2ObjectSortedMap<V> headMap(Float var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float2ObjectSortedMap<V> tailMap(Float var1) {
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
      return this.float2ObjectEntrySet();
   }

   ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet();

   FloatSortedSet keySet();

   ObjectCollection<V> values();

   FloatComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Float2ObjectMap.Entry<V>>, Float2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> fastIterator(Float2ObjectMap.Entry<V> var1);
   }
}
