package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.SortedMap;

public interface Char2ShortSortedMap extends Char2ShortMap, SortedMap<Character, Short> {
   Char2ShortSortedMap subMap(char var1, char var2);

   Char2ShortSortedMap headMap(char var1);

   Char2ShortSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   /** @deprecated */
   @Deprecated
   default Char2ShortSortedMap subMap(Character var1, Character var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Char2ShortSortedMap headMap(Character var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Char2ShortSortedMap tailMap(Character var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Character, Short>> entrySet() {
      return this.char2ShortEntrySet();
   }

   ObjectSortedSet<Char2ShortMap.Entry> char2ShortEntrySet();

   CharSortedSet keySet();

   ShortCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2ShortMap.Entry>, Char2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2ShortMap.Entry> fastIterator(Char2ShortMap.Entry var1);
   }
}
