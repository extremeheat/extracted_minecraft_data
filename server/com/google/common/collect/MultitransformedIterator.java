package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.NoSuchElementException;

@GwtCompatible
abstract class MultitransformedIterator<F, T> implements Iterator<T> {
   final Iterator<? extends F> backingIterator;
   private Iterator<? extends T> current = Iterators.emptyIterator();
   private Iterator<? extends T> removeFrom;

   MultitransformedIterator(Iterator<? extends F> var1) {
      super();
      this.backingIterator = (Iterator)Preconditions.checkNotNull(var1);
   }

   abstract Iterator<? extends T> transform(F var1);

   public boolean hasNext() {
      Preconditions.checkNotNull(this.current);
      if (this.current.hasNext()) {
         return true;
      } else {
         do {
            if (!this.backingIterator.hasNext()) {
               return false;
            }

            Preconditions.checkNotNull(this.current = this.transform(this.backingIterator.next()));
         } while(!this.current.hasNext());

         return true;
      }
   }

   public T next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         this.removeFrom = this.current;
         return this.current.next();
      }
   }

   public void remove() {
      CollectPreconditions.checkRemove(this.removeFrom != null);
      this.removeFrom.remove();
      this.removeFrom = null;
   }
}
