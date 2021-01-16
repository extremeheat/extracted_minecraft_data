package it.unimi.dsi.fastutil.longs;

import java.util.SortedSet;

public interface LongSortedSet extends LongSet, SortedSet<Long>, LongBidirectionalIterable {
   LongBidirectionalIterator iterator(long var1);

   LongBidirectionalIterator iterator();

   LongSortedSet subSet(long var1, long var3);

   LongSortedSet headSet(long var1);

   LongSortedSet tailSet(long var1);

   LongComparator comparator();

   long firstLong();

   long lastLong();

   /** @deprecated */
   @Deprecated
   default LongSortedSet subSet(Long var1, Long var2) {
      return this.subSet(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default LongSortedSet headSet(Long var1) {
      return this.headSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default LongSortedSet tailSet(Long var1) {
      return this.tailSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   /** @deprecated */
   @Deprecated
   default Long last() {
      return this.lastLong();
   }
}
