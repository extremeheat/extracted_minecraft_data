package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;

public interface Char2ReferenceSortedMap<V> extends Char2ReferenceMap<V>, SortedMap<Character, V> {
   Char2ReferenceSortedMap<V> subMap(char var1, char var2);

   Char2ReferenceSortedMap<V> headMap(char var1);

   Char2ReferenceSortedMap<V> tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   /** @deprecated */
   @Deprecated
   default Char2ReferenceSortedMap<V> subMap(Character var1, Character var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Char2ReferenceSortedMap<V> headMap(Character var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Char2ReferenceSortedMap<V> tailMap(Character var1) {
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
   default ObjectSortedSet<java.util.Map.Entry<Character, V>> entrySet() {
      return this.char2ReferenceEntrySet();
   }

   ObjectSortedSet<Char2ReferenceMap.Entry<V>> char2ReferenceEntrySet();

   CharSortedSet keySet();

   ReferenceCollection<V> values();

   CharComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Char2ReferenceMap.Entry<V>>, Char2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> fastIterator(Char2ReferenceMap.Entry<V> var1);
   }
}
