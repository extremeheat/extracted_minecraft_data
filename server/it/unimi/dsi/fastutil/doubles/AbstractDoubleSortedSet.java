package it.unimi.dsi.fastutil.doubles;

public abstract class AbstractDoubleSortedSet extends AbstractDoubleSet implements DoubleSortedSet {
   protected AbstractDoubleSortedSet() {
      super();
   }

   public abstract DoubleBidirectionalIterator iterator();
}
