package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Char2DoubleSortedMap extends Char2DoubleMap, SortedMap<Character, Double> {
   Char2DoubleSortedMap subMap(char var1, char var2);

   Char2DoubleSortedMap headMap(char var1);

   Char2DoubleSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   /** @deprecated */
   @Deprecated
   default Char2DoubleSortedMap subMap(Character var1, Character var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Char2DoubleSortedMap headMap(Character var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Char2DoubleSortedMap tailMap(Character var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Character, Double>> entrySet() {
      return this.char2DoubleEntrySet();
   }

   ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet();

   CharSortedSet keySet();

   DoubleCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2DoubleMap.Entry>, Char2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2DoubleMap.Entry> fastIterator(Char2DoubleMap.Entry var1);
   }
}
