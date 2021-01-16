package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.SortedMap;

public interface Float2ShortSortedMap extends Float2ShortMap, SortedMap<Float, Short> {
   Float2ShortSortedMap subMap(float var1, float var2);

   Float2ShortSortedMap headMap(float var1);

   Float2ShortSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   /** @deprecated */
   @Deprecated
   default Float2ShortSortedMap subMap(Float var1, Float var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float2ShortSortedMap headMap(Float var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float2ShortSortedMap tailMap(Float var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Float, Short>> entrySet() {
      return this.float2ShortEntrySet();
   }

   ObjectSortedSet<Float2ShortMap.Entry> float2ShortEntrySet();

   FloatSortedSet keySet();

   ShortCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2ShortMap.Entry>, Float2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2ShortMap.Entry> fastIterator(Float2ShortMap.Entry var1);
   }
}
