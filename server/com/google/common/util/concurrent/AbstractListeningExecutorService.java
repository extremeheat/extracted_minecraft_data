package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import javax.annotation.Nullable;

@Beta
@CanIgnoreReturnValue
@GwtIncompatible
public abstract class AbstractListeningExecutorService extends AbstractExecutorService implements ListeningExecutorService {
   public AbstractListeningExecutorService() {
      super();
   }

   protected final <T> RunnableFuture<T> newTaskFor(Runnable var1, T var2) {
      return TrustedListenableFutureTask.create(var1, var2);
   }

   protected final <T> RunnableFuture<T> newTaskFor(Callable<T> var1) {
      return TrustedListenableFutureTask.create(var1);
   }

   public ListenableFuture<?> submit(Runnable var1) {
      return (ListenableFuture)super.submit(var1);
   }

   public <T> ListenableFuture<T> submit(Runnable var1, @Nullable T var2) {
      return (ListenableFuture)super.submit(var1, var2);
   }

   public <T> ListenableFuture<T> submit(Callable<T> var1) {
      return (ListenableFuture)super.submit(var1);
   }
}
