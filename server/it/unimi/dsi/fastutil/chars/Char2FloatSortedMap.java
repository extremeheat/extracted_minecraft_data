package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Char2FloatSortedMap extends Char2FloatMap, SortedMap<Character, Float> {
   Char2FloatSortedMap subMap(char var1, char var2);

   Char2FloatSortedMap headMap(char var1);

   Char2FloatSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   /** @deprecated */
   @Deprecated
   default Char2FloatSortedMap subMap(Character var1, Character var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Char2FloatSortedMap headMap(Character var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Char2FloatSortedMap tailMap(Character var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Character, Float>> entrySet() {
      return this.char2FloatEntrySet();
   }

   ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet();

   CharSortedSet keySet();

   FloatCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2FloatMap.Entry>, Char2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2FloatMap.Entry> fastIterator(Char2FloatMap.Entry var1);
   }
}
