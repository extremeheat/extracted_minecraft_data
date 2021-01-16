package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtIncompatible
public abstract class ForwardingCheckedFuture<V, X extends Exception> extends ForwardingListenableFuture<V> implements CheckedFuture<V, X> {
   public ForwardingCheckedFuture() {
      super();
   }

   @CanIgnoreReturnValue
   public V checkedGet() throws X {
      return this.delegate().checkedGet();
   }

   @CanIgnoreReturnValue
   public V checkedGet(long var1, TimeUnit var3) throws TimeoutException, X {
      return this.delegate().checkedGet(var1, var3);
   }

   protected abstract CheckedFuture<V, X> delegate();

   @Beta
   public abstract static class SimpleForwardingCheckedFuture<V, X extends Exception> extends ForwardingCheckedFuture<V, X> {
      private final CheckedFuture<V, X> delegate;

      protected SimpleForwardingCheckedFuture(CheckedFuture<V, X> var1) {
         super();
         this.delegate = (CheckedFuture)Preconditions.checkNotNull(var1);
      }

      protected final CheckedFuture<V, X> delegate() {
         return this.delegate;
      }
   }
}
