package it.unimi.dsi.fastutil.ints;

import java.util.SortedSet;

public interface IntSortedSet extends IntSet, SortedSet<Integer>, IntBidirectionalIterable {
   IntBidirectionalIterator iterator(int var1);

   IntBidirectionalIterator iterator();

   IntSortedSet subSet(int var1, int var2);

   IntSortedSet headSet(int var1);

   IntSortedSet tailSet(int var1);

   IntComparator comparator();

   int firstInt();

   int lastInt();

   /** @deprecated */
   @Deprecated
   default IntSortedSet subSet(Integer var1, Integer var2) {
      return this.subSet(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default IntSortedSet headSet(Integer var1) {
      return this.headSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default IntSortedSet tailSet(Integer var1) {
      return this.tailSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   /** @deprecated */
   @Deprecated
   default Integer last() {
      return this.lastInt();
   }
}
