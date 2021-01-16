package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingObject;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@CanIgnoreReturnValue
@GwtCompatible
public abstract class ForwardingFuture<V> extends ForwardingObject implements Future<V> {
   protected ForwardingFuture() {
      super();
   }

   protected abstract Future<? extends V> delegate();

   public boolean cancel(boolean var1) {
      return this.delegate().cancel(var1);
   }

   public boolean isCancelled() {
      return this.delegate().isCancelled();
   }

   public boolean isDone() {
      return this.delegate().isDone();
   }

   public V get() throws InterruptedException, ExecutionException {
      return this.delegate().get();
   }

   public V get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
      return this.delegate().get(var1, var3);
   }

   public abstract static class SimpleForwardingFuture<V> extends ForwardingFuture<V> {
      private final Future<V> delegate;

      protected SimpleForwardingFuture(Future<V> var1) {
         super();
         this.delegate = (Future)Preconditions.checkNotNull(var1);
      }

      protected final Future<V> delegate() {
         return this.delegate;
      }
   }
}
