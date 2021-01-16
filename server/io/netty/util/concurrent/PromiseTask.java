package io.netty.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

class PromiseTask<V> extends DefaultPromise<V> implements RunnableFuture<V> {
   protected final Callable<V> task;

   static <T> Callable<T> toCallable(Runnable var0, T var1) {
      return new PromiseTask.RunnableAdapter(var0, var1);
   }

   PromiseTask(EventExecutor var1, Runnable var2, V var3) {
      this(var1, toCallable(var2, var3));
   }

   PromiseTask(EventExecutor var1, Callable<V> var2) {
      super(var1);
      this.task = var2;
   }

   public final int hashCode() {
      return System.identityHashCode(this);
   }

   public final boolean equals(Object var1) {
      return this == var1;
   }

   public void run() {
      try {
         if (this.setUncancellableInternal()) {
            Object var1 = this.task.call();
            this.setSuccessInternal(var1);
         }
      } catch (Throwable var2) {
         this.setFailureInternal(var2);
      }

   }

   public final Promise<V> setFailure(Throwable var1) {
      throw new IllegalStateException();
   }

   protected final Promise<V> setFailureInternal(Throwable var1) {
      super.setFailure(var1);
      return this;
   }

   public final boolean tryFailure(Throwable var1) {
      return false;
   }

   protected final boolean tryFailureInternal(Throwable var1) {
      return super.tryFailure(var1);
   }

   public final Promise<V> setSuccess(V var1) {
      throw new IllegalStateException();
   }

   protected final Promise<V> setSuccessInternal(V var1) {
      super.setSuccess(var1);
      return this;
   }

   public final boolean trySuccess(V var1) {
      return false;
   }

   protected final boolean trySuccessInternal(V var1) {
      return super.trySuccess(var1);
   }

   public final boolean setUncancellable() {
      throw new IllegalStateException();
   }

   protected final boolean setUncancellableInternal() {
      return super.setUncancellable();
   }

   protected StringBuilder toStringBuilder() {
      StringBuilder var1 = super.toStringBuilder();
      var1.setCharAt(var1.length() - 1, ',');
      return var1.append(" task: ").append(this.task).append(')');
   }

   private static final class RunnableAdapter<T> implements Callable<T> {
      final Runnable task;
      final T result;

      RunnableAdapter(Runnable var1, T var2) {
         super();
         this.task = var1;
         this.result = var2;
      }

      public T call() {
         this.task.run();
         return this.result;
      }

      public String toString() {
         return "Callable(task: " + this.task + ", result: " + this.result + ')';
      }
   }
}
