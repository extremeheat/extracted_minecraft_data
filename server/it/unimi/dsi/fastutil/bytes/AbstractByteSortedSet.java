package it.unimi.dsi.fastutil.bytes;

public abstract class AbstractByteSortedSet extends AbstractByteSet implements ByteSortedSet {
   protected AbstractByteSortedSet() {
      super();
   }

   public abstract ByteBidirectionalIterator iterator();
}
