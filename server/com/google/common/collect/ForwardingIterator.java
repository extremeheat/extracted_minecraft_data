package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;

@GwtCompatible
public abstract class ForwardingIterator<T> extends ForwardingObject implements Iterator<T> {
   protected ForwardingIterator() {
      super();
   }

   protected abstract Iterator<T> delegate();

   public boolean hasNext() {
      return this.delegate().hasNext();
   }

   @CanIgnoreReturnValue
   public T next() {
      return this.delegate().next();
   }

   public void remove() {
      this.delegate().remove();
   }
}
