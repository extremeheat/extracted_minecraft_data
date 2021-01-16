package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Byte2ObjectSortedMap<V> extends Byte2ObjectMap<V>, SortedMap<Byte, V> {
   Byte2ObjectSortedMap<V> subMap(byte var1, byte var2);

   Byte2ObjectSortedMap<V> headMap(byte var1);

   Byte2ObjectSortedMap<V> tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   /** @deprecated */
   @Deprecated
   default Byte2ObjectSortedMap<V> subMap(Byte var1, Byte var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte2ObjectSortedMap<V> headMap(Byte var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte2ObjectSortedMap<V> tailMap(Byte var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte firstKey() {
      return this.firstByteKey();
   }

   /** @deprecated */
   @Deprecated
   default Byte lastKey() {
      return this.lastByteKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Byte, V>> entrySet() {
      return this.byte2ObjectEntrySet();
   }

   ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet();

   ByteSortedSet keySet();

   ObjectCollection<V> values();

   ByteComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Byte2ObjectMap.Entry<V>>, Byte2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> fastIterator(Byte2ObjectMap.Entry<V> var1);
   }
}
