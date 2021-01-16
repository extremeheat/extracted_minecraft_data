package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Float2ByteSortedMap extends Float2ByteMap, SortedMap<Float, Byte> {
   Float2ByteSortedMap subMap(float var1, float var2);

   Float2ByteSortedMap headMap(float var1);

   Float2ByteSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   /** @deprecated */
   @Deprecated
   default Float2ByteSortedMap subMap(Float var1, Float var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float2ByteSortedMap headMap(Float var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float2ByteSortedMap tailMap(Float var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Float, Byte>> entrySet() {
      return this.float2ByteEntrySet();
   }

   ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet();

   FloatSortedSet keySet();

   ByteCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2ByteMap.Entry>, Float2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2ByteMap.Entry> fastIterator(Float2ByteMap.Entry var1);
   }
}
