package it.unimi.dsi.fastutil.ints;

public abstract class AbstractIntSortedSet extends AbstractIntSet implements IntSortedSet {
   protected AbstractIntSortedSet() {
      super();
   }

   public abstract IntBidirectionalIterator iterator();
}
