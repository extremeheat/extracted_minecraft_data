package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingObject;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@CanIgnoreReturnValue
@GwtIncompatible
public abstract class ForwardingExecutorService extends ForwardingObject implements ExecutorService {
   protected ForwardingExecutorService() {
      super();
   }

   protected abstract ExecutorService delegate();

   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      return this.delegate().awaitTermination(var1, var3);
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
      return this.delegate().invokeAll(var1);
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.delegate().invokeAll(var1, var2, var4);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException {
      return this.delegate().invokeAny(var1);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException {
      return this.delegate().invokeAny(var1, var2, var4);
   }

   public boolean isShutdown() {
      return this.delegate().isShutdown();
   }

   public boolean isTerminated() {
      return this.delegate().isTerminated();
   }

   public void shutdown() {
      this.delegate().shutdown();
   }

   public List<Runnable> shutdownNow() {
      return this.delegate().shutdownNow();
   }

   public void execute(Runnable var1) {
      this.delegate().execute(var1);
   }

   public <T> Future<T> submit(Callable<T> var1) {
      return this.delegate().submit(var1);
   }

   public Future<?> submit(Runnable var1) {
      return this.delegate().submit(var1);
   }

   public <T> Future<T> submit(Runnable var1, T var2) {
      return this.delegate().submit(var1, var2);
   }
}
