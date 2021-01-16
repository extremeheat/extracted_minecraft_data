package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Float2DoubleSortedMap extends Float2DoubleMap, SortedMap<Float, Double> {
   Float2DoubleSortedMap subMap(float var1, float var2);

   Float2DoubleSortedMap headMap(float var1);

   Float2DoubleSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   /** @deprecated */
   @Deprecated
   default Float2DoubleSortedMap subMap(Float var1, Float var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float2DoubleSortedMap headMap(Float var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float2DoubleSortedMap tailMap(Float var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Float, Double>> entrySet() {
      return this.float2DoubleEntrySet();
   }

   ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet();

   FloatSortedSet keySet();

   DoubleCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2DoubleMap.Entry>, Float2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2DoubleMap.Entry> fastIterator(Float2DoubleMap.Entry var1);
   }
}
