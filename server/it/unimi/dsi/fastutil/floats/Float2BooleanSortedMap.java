package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Float2BooleanSortedMap extends Float2BooleanMap, SortedMap<Float, Boolean> {
   Float2BooleanSortedMap subMap(float var1, float var2);

   Float2BooleanSortedMap headMap(float var1);

   Float2BooleanSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   /** @deprecated */
   @Deprecated
   default Float2BooleanSortedMap subMap(Float var1, Float var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float2BooleanSortedMap headMap(Float var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float2BooleanSortedMap tailMap(Float var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Float, Boolean>> entrySet() {
      return this.float2BooleanEntrySet();
   }

   ObjectSortedSet<Float2BooleanMap.Entry> float2BooleanEntrySet();

   FloatSortedSet keySet();

   BooleanCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2BooleanMap.Entry>, Float2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2BooleanMap.Entry> fastIterator(Float2BooleanMap.Entry var1);
   }
}
