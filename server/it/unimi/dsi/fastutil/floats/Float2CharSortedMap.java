package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Float2CharSortedMap extends Float2CharMap, SortedMap<Float, Character> {
   Float2CharSortedMap subMap(float var1, float var2);

   Float2CharSortedMap headMap(float var1);

   Float2CharSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   /** @deprecated */
   @Deprecated
   default Float2CharSortedMap subMap(Float var1, Float var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float2CharSortedMap headMap(Float var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float2CharSortedMap tailMap(Float var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Float, Character>> entrySet() {
      return this.float2CharEntrySet();
   }

   ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet();

   FloatSortedSet keySet();

   CharCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2CharMap.Entry>, Float2CharMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2CharMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2CharMap.Entry> fastIterator(Float2CharMap.Entry var1);
   }
}
