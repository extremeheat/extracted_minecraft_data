package it.unimi.dsi.fastutil.doubles;

import java.util.SortedSet;

public interface DoubleSortedSet extends DoubleSet, SortedSet<Double>, DoubleBidirectionalIterable {
   DoubleBidirectionalIterator iterator(double var1);

   DoubleBidirectionalIterator iterator();

   DoubleSortedSet subSet(double var1, double var3);

   DoubleSortedSet headSet(double var1);

   DoubleSortedSet tailSet(double var1);

   DoubleComparator comparator();

   double firstDouble();

   double lastDouble();

   /** @deprecated */
   @Deprecated
   default DoubleSortedSet subSet(Double var1, Double var2) {
      return this.subSet(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default DoubleSortedSet headSet(Double var1) {
      return this.headSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default DoubleSortedSet tailSet(Double var1) {
      return this.tailSet(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   /** @deprecated */
   @Deprecated
   default Double last() {
      return this.lastDouble();
   }
}
