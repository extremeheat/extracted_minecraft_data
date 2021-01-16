package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@CanIgnoreReturnValue
@GwtIncompatible
public interface ListeningExecutorService extends ExecutorService {
   <T> ListenableFuture<T> submit(Callable<T> var1);

   ListenableFuture<?> submit(Runnable var1);

   <T> ListenableFuture<T> submit(Runnable var1, T var2);

   <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException;

   <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException;
}
