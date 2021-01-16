package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
abstract class ImmediateFuture<V> implements ListenableFuture<V> {
   private static final Logger log = Logger.getLogger(ImmediateFuture.class.getName());

   ImmediateFuture() {
      super();
   }

   public void addListener(Runnable var1, Executor var2) {
      Preconditions.checkNotNull(var1, "Runnable was null.");
      Preconditions.checkNotNull(var2, "Executor was null.");

      try {
         var2.execute(var1);
      } catch (RuntimeException var4) {
         log.log(Level.SEVERE, "RuntimeException while executing runnable " + var1 + " with executor " + var2, var4);
      }

   }

   public boolean cancel(boolean var1) {
      return false;
   }

   public abstract V get() throws ExecutionException;

   public V get(long var1, TimeUnit var3) throws ExecutionException {
      Preconditions.checkNotNull(var3);
      return this.get();
   }

   public boolean isCancelled() {
      return false;
   }

   public boolean isDone() {
      return true;
   }

   @GwtIncompatible
   static class ImmediateFailedCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X> {
      private final X thrown;

      ImmediateFailedCheckedFuture(X var1) {
         super();
         this.thrown = var1;
      }

      public V get() throws ExecutionException {
         throw new ExecutionException(this.thrown);
      }

      public V checkedGet() throws X {
         throw this.thrown;
      }

      public V checkedGet(long var1, TimeUnit var3) throws X {
         Preconditions.checkNotNull(var3);
         throw this.thrown;
      }
   }

   static final class ImmediateCancelledFuture<V> extends AbstractFuture.TrustedFuture<V> {
      ImmediateCancelledFuture() {
         super();
         this.cancel(false);
      }
   }

   static final class ImmediateFailedFuture<V> extends AbstractFuture.TrustedFuture<V> {
      ImmediateFailedFuture(Throwable var1) {
         super();
         this.setException(var1);
      }
   }

   @GwtIncompatible
   static class ImmediateSuccessfulCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X> {
      @Nullable
      private final V value;

      ImmediateSuccessfulCheckedFuture(@Nullable V var1) {
         super();
         this.value = var1;
      }

      public V get() {
         return this.value;
      }

      public V checkedGet() {
         return this.value;
      }

      public V checkedGet(long var1, TimeUnit var3) {
         Preconditions.checkNotNull(var3);
         return this.value;
      }
   }

   static class ImmediateSuccessfulFuture<V> extends ImmediateFuture<V> {
      static final ImmediateFuture.ImmediateSuccessfulFuture<Object> NULL = new ImmediateFuture.ImmediateSuccessfulFuture((Object)null);
      @Nullable
      private final V value;

      ImmediateSuccessfulFuture(@Nullable V var1) {
         super();
         this.value = var1;
      }

      public V get() {
         return this.value;
      }
   }
}
