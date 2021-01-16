package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Int2ByteSortedMap extends Int2ByteMap, SortedMap<Integer, Byte> {
   Int2ByteSortedMap subMap(int var1, int var2);

   Int2ByteSortedMap headMap(int var1);

   Int2ByteSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   /** @deprecated */
   @Deprecated
   default Int2ByteSortedMap subMap(Integer var1, Integer var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Int2ByteSortedMap headMap(Integer var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Int2ByteSortedMap tailMap(Integer var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Integer, Byte>> entrySet() {
      return this.int2ByteEntrySet();
   }

   ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet();

   IntSortedSet keySet();

   ByteCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2ByteMap.Entry>, Int2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2ByteMap.Entry> fastIterator(Int2ByteMap.Entry var1);
   }
}
