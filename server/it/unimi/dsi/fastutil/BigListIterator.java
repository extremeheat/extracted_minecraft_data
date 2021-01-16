package it.unimi.dsi.fastutil;

public interface BigListIterator<K> extends BidirectionalIterator<K> {
   long nextIndex();

   long previousIndex();

   default void set(K var1) {
      throw new UnsupportedOperationException();
   }

   default void add(K var1) {
      throw new UnsupportedOperationException();
   }
}
