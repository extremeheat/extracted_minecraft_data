package it.unimi.dsi.fastutil.floats;

public abstract class AbstractFloatSortedSet extends AbstractFloatSet implements FloatSortedSet {
   protected AbstractFloatSortedSet() {
      super();
   }

   public abstract FloatBidirectionalIterator iterator();
}
