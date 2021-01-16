package org.apache.commons.lang3.concurrent;

public abstract class LazyInitializer<T> implements ConcurrentInitializer<T> {
   private volatile T object;

   public LazyInitializer() {
      super();
   }

   public T get() throws ConcurrentException {
      Object var1 = this.object;
      if (var1 == null) {
         synchronized(this) {
            var1 = this.object;
            if (var1 == null) {
               this.object = var1 = this.initialize();
            }
         }
      }

      return var1;
   }

   protected abstract T initialize() throws ConcurrentException;
}
