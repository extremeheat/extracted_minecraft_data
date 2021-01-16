package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Char2BooleanSortedMap extends Char2BooleanMap, SortedMap<Character, Boolean> {
   Char2BooleanSortedMap subMap(char var1, char var2);

   Char2BooleanSortedMap headMap(char var1);

   Char2BooleanSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   /** @deprecated */
   @Deprecated
   default Char2BooleanSortedMap subMap(Character var1, Character var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Char2BooleanSortedMap headMap(Character var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Char2BooleanSortedMap tailMap(Character var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Character, Boolean>> entrySet() {
      return this.char2BooleanEntrySet();
   }

   ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet();

   CharSortedSet keySet();

   BooleanCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2BooleanMap.Entry>, Char2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2BooleanMap.Entry> fastIterator(Char2BooleanMap.Entry var1);
   }
}
