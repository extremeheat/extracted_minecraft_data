package it.unimi.dsi.fastutil.chars;

import java.util.SortedSet;

public interface CharSortedSet extends CharSet, SortedSet<Character>, CharBidirectionalIterable {
   CharBidirectionalIterator iterator(char var1);

   CharBidirectionalIterator iterator();

   CharSortedSet subSet(char var1, char var2);

   CharSortedSet headSet(char var1);

   CharSortedSet tailSet(char var1);

   CharComparator comparator();

   char firstChar();

   char lastChar();

   /** @deprecated */
   @Deprecated
   default CharSortedSet subSet(Character var1, Character var2) {
      return this.subSet(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default CharSortedSet headSet(Character var1) {
      return this.headSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default CharSortedSet tailSet(Character var1) {
      return this.tailSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   /** @deprecated */
   @Deprecated
   default Character last() {
      return this.lastChar();
   }
}
