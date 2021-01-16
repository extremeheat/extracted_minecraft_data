package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;

public interface Byte2ReferenceSortedMap<V> extends Byte2ReferenceMap<V>, SortedMap<Byte, V> {
   Byte2ReferenceSortedMap<V> subMap(byte var1, byte var2);

   Byte2ReferenceSortedMap<V> headMap(byte var1);

   Byte2ReferenceSortedMap<V> tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   /** @deprecated */
   @Deprecated
   default Byte2ReferenceSortedMap<V> subMap(Byte var1, Byte var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte2ReferenceSortedMap<V> headMap(Byte var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte2ReferenceSortedMap<V> tailMap(Byte var1) {
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
      return this.byte2ReferenceEntrySet();
   }

   ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet();

   ByteSortedSet keySet();

   ReferenceCollection<V> values();

   ByteComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Byte2ReferenceMap.Entry<V>>, Byte2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> fastIterator(Byte2ReferenceMap.Entry<V> var1);
   }
}
