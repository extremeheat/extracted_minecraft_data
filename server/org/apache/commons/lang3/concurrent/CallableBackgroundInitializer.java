package org.apache.commons.lang3.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class CallableBackgroundInitializer<T> extends BackgroundInitializer<T> {
   private final Callable<T> callable;

   public CallableBackgroundInitializer(Callable<T> var1) {
      super();
      this.checkCallable(var1);
      this.callable = var1;
   }

   public CallableBackgroundInitializer(Callable<T> var1, ExecutorService var2) {
      super(var2);
      this.checkCallable(var1);
      this.callable = var1;
   }

   protected T initialize() throws Exception {
      return this.callable.call();
   }

   private void checkCallable(Callable<T> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Callable must not be null!");
      }
   }
}
