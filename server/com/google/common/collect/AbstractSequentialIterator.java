package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class AbstractSequentialIterator<T> extends UnmodifiableIterator<T> {
   private T nextOrNull;

   protected AbstractSequentialIterator(@Nullable T var1) {
      super();
      this.nextOrNull = var1;
   }

   protected abstract T computeNext(T var1);

   public final boolean hasNext() {
      return this.nextOrNull != null;
   }

   public final T next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         Object var1;
         try {
            var1 = this.nextOrNull;
         } finally {
            this.nextOrNull = this.computeNext(this.nextOrNull);
         }

         return var1;
      }
   }
}
