package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AtomicInitializer<T> implements ConcurrentInitializer<T> {
   private final AtomicReference<T> reference = new AtomicReference();

   public AtomicInitializer() {
      super();
   }

   public T get() throws ConcurrentException {
      Object var1 = this.reference.get();
      if (var1 == null) {
         var1 = this.initialize();
         if (!this.reference.compareAndSet((Object)null, var1)) {
            var1 = this.reference.get();
         }
      }

      return var1;
   }

   protected abstract T initialize() throws ConcurrentException;
}
