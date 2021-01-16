package it.unimi.dsi.fastutil.floats;

import java.util.SortedSet;

public interface FloatSortedSet extends FloatSet, SortedSet<Float>, FloatBidirectionalIterable {
   FloatBidirectionalIterator iterator(float var1);

   FloatBidirectionalIterator iterator();

   FloatSortedSet subSet(float var1, float var2);

   FloatSortedSet headSet(float var1);

   FloatSortedSet tailSet(float var1);

   FloatComparator comparator();

   float firstFloat();

   float lastFloat();

   /** @deprecated */
   @Deprecated
   default FloatSortedSet subSet(Float var1, Float var2) {
      return this.subSet(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default FloatSortedSet headSet(Float var1) {
      return this.headSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default FloatSortedSet tailSet(Float var1) {
      return this.tailSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   /** @deprecated */
   @Deprecated
   default Float last() {
      return this.lastFloat();
   }
}
