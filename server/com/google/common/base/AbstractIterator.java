package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractIterator<T> implements Iterator<T> {
   private AbstractIterator.State state;
   private T next;

   protected AbstractIterator() {
      super();
      this.state = AbstractIterator.State.NOT_READY;
   }

   protected abstract T computeNext();

   @Nullable
   @CanIgnoreReturnValue
   protected final T endOfData() {
      this.state = AbstractIterator.State.DONE;
      return null;
   }

   public final boolean hasNext() {
      Preconditions.checkState(this.state != AbstractIterator.State.FAILED);
      switch(this.state) {
      case READY:
         return true;
      case DONE:
         return false;
      default:
         return this.tryToComputeNext();
      }
   }

   private boolean tryToComputeNext() {
      this.state = AbstractIterator.State.FAILED;
      this.next = this.computeNext();
      if (this.state != AbstractIterator.State.DONE) {
         this.state = AbstractIterator.State.READY;
         return true;
      } else {
         return false;
      }
   }

   public final T next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         this.state = AbstractIterator.State.NOT_READY;
         Object var1 = this.next;
         this.next = null;
         return var1;
      }
   }

   public final void remove() {
      throw new UnsupportedOperationException();
   }

   private static enum State {
      READY,
      NOT_READY,
      DONE,
      FAILED;

      private State() {
      }
   }
}
