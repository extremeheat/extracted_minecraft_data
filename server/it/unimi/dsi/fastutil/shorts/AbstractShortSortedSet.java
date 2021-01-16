package it.unimi.dsi.fastutil.shorts;

public abstract class AbstractShortSortedSet extends AbstractShortSet implements ShortSortedSet {
   protected AbstractShortSortedSet() {
      super();
   }

   public abstract ShortBidirectionalIterator iterator();
}
