package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Char2LongSortedMap extends Char2LongMap, SortedMap<Character, Long> {
   Char2LongSortedMap subMap(char var1, char var2);

   Char2LongSortedMap headMap(char var1);

   Char2LongSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   /** @deprecated */
   @Deprecated
   default Char2LongSortedMap subMap(Character var1, Character var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Char2LongSortedMap headMap(Character var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Char2LongSortedMap tailMap(Character var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Character, Long>> entrySet() {
      return this.char2LongEntrySet();
   }

   ObjectSortedSet<Char2LongMap.Entry> char2LongEntrySet();

   CharSortedSet keySet();

   LongCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2LongMap.Entry>, Char2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2LongMap.Entry> fastIterator(Char2LongMap.Entry var1);
   }
}
