package io.netty.util.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractFuture<V> implements Future<V> {
   public AbstractFuture() {
      super();
   }

   public V get() throws InterruptedException, ExecutionException {
      this.await();
      Throwable var1 = this.cause();
      if (var1 == null) {
         return this.getNow();
      } else if (var1 instanceof CancellationException) {
         throw (CancellationException)var1;
      } else {
         throw new ExecutionException(var1);
      }
   }

   public V get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
      if (this.await(var1, var3)) {
         Throwable var4 = this.cause();
         if (var4 == null) {
            return this.getNow();
         } else if (var4 instanceof CancellationException) {
            throw (CancellationException)var4;
         } else {
            throw new ExecutionException(var4);
         }
      } else {
         throw new TimeoutException();
      }
   }
}
