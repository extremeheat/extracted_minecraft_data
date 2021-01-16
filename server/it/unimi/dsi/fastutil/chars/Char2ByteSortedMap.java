package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Char2ByteSortedMap extends Char2ByteMap, SortedMap<Character, Byte> {
   Char2ByteSortedMap subMap(char var1, char var2);

   Char2ByteSortedMap headMap(char var1);

   Char2ByteSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   /** @deprecated */
   @Deprecated
   default Char2ByteSortedMap subMap(Character var1, Character var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Char2ByteSortedMap headMap(Character var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Char2ByteSortedMap tailMap(Character var1) {
      return this.tailMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Character firstKey() {
      return this.firstCharKey();
   }

   /** @deprecated */
   @Deprecated
   default Character lastKey() {
      return this.lastCharKey();
   }

   /** @deprecated */
   @Deprecated
   default ObjectSortedSet<java.util.Map.Entry<Character, Byte>> entrySet() {
      return this.char2ByteEntrySet();
   }

   ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet();

   CharSortedSet keySet();

   ByteCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2ByteMap.Entry>, Char2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2ByteMap.Entry> fastIterator(Char2ByteMap.Entry var1);
   }
}
