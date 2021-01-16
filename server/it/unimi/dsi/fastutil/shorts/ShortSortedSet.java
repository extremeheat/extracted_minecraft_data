package it.unimi.dsi.fastutil.shorts;

import java.util.SortedSet;

public interface ShortSortedSet extends ShortSet, SortedSet<Short>, ShortBidirectionalIterable {
   ShortBidirectionalIterator iterator(short var1);

   ShortBidirectionalIterator iterator();

   ShortSortedSet subSet(short var1, short var2);

   ShortSortedSet headSet(short var1);

   ShortSortedSet tailSet(short var1);

   ShortComparator comparator();

   short firstShort();

   short lastShort();

   /** @deprecated */
   @Deprecated
   default ShortSortedSet subSet(Short var1, Short var2) {
      return this.subSet(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default ShortSortedSet headSet(Short var1) {
      return this.headSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default ShortSortedSet tailSet(Short var1) {
      return this.tailSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   /** @deprecated */
   @Deprecated
   default Short last() {
      return this.lastShort();
   }
}
