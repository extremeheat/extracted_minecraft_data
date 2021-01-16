package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Float2FloatSortedMap extends Float2FloatMap, SortedMap<Float, Float> {
   Float2FloatSortedMap subMap(float var1, float var2);

   Float2FloatSortedMap headMap(float var1);

   Float2FloatSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   /** @deprecated */
   @Deprecated
   default Float2FloatSortedMap subMap(Float var1, Float var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float2FloatSortedMap headMap(Float var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float2FloatSortedMap tailMap(Float var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Float, Float>> entrySet() {
      return this.float2FloatEntrySet();
   }

   ObjectSortedSet<Float2FloatMap.Entry> float2FloatEntrySet();

   FloatSortedSet keySet();

   FloatCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2FloatMap.Entry>, Float2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2FloatMap.Entry> fastIterator(Float2FloatMap.Entry var1);
   }
}
