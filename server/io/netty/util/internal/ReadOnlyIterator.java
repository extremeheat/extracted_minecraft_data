package io.netty.util.internal;

import java.util.Iterator;

public final class ReadOnlyIterator<T> implements Iterator<T> {
   private final Iterator<? extends T> iterator;

   public ReadOnlyIterator(Iterator<? extends T> var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("iterator");
      } else {
         this.iterator = var1;
      }
   }

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public T next() {
      return this.iterator.next();
   }

   public void remove() {
      throw new UnsupportedOperationException("read-only");
   }
}
