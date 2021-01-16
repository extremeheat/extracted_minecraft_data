package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Float2IntSortedMap extends Float2IntMap, SortedMap<Float, Integer> {
   Float2IntSortedMap subMap(float var1, float var2);

   Float2IntSortedMap headMap(float var1);

   Float2IntSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   /** @deprecated */
   @Deprecated
   default Float2IntSortedMap subMap(Float var1, Float var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float2IntSortedMap headMap(Float var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float2IntSortedMap tailMap(Float var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Float, Integer>> entrySet() {
      return this.float2IntEntrySet();
   }

   ObjectSortedSet<Float2IntMap.Entry> float2IntEntrySet();

   FloatSortedSet keySet();

   IntCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2IntMap.Entry>, Float2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2IntMap.Entry> fastIterator(Float2IntMap.Entry var1);
   }
}
