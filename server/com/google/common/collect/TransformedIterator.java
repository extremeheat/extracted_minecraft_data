package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Iterator;

@GwtCompatible
abstract class TransformedIterator<F, T> implements Iterator<T> {
   final Iterator<? extends F> backingIterator;

   TransformedIterator(Iterator<? extends F> var1) {
      super();
      this.backingIterator = (Iterator)Preconditions.checkNotNull(var1);
   }

   abstract T transform(F var1);

   public final boolean hasNext() {
      return this.backingIterator.hasNext();
   }

   public final T next() {
      return this.transform(this.backingIterator.next());
   }

   public final void remove() {
      this.backingIterator.remove();
   }
}
