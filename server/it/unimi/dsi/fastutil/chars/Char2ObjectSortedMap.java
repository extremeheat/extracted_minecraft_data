package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Char2ObjectSortedMap<V> extends Char2ObjectMap<V>, SortedMap<Character, V> {
   Char2ObjectSortedMap<V> subMap(char var1, char var2);

   Char2ObjectSortedMap<V> headMap(char var1);

   Char2ObjectSortedMap<V> tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   /** @deprecated */
   @Deprecated
   default Char2ObjectSortedMap<V> subMap(Character var1, Character var2) {
      return this.subMap(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Char2ObjectSortedMap<V> headMap(Character var1) {
      return this.headMap(var1);
   }

   /** @deprecated */
   @Deprecated
   default Char2ObjectSortedMap<V> tailMap(Character var1) {
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
      return this.char2ObjectEntrySet();
   }

   ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet();

   CharSortedSet keySet();

   ObjectCollection<V> values();

   CharComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Char2ObjectMap.Entry<V>>, Char2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> fastIterator(Char2ObjectMap.Entry<V> var1);
   }
}
