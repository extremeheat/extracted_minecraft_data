package io.netty.util.concurrent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractEventExecutorGroup implements EventExecutorGroup {
   public AbstractEventExecutorGroup() {
      super();
   }

   public Future<?> submit(Runnable var1) {
      return this.next().submit(var1);
   }

   public <T> Future<T> submit(Runnable var1, T var2) {
      return this.next().submit(var1, var2);
   }

   public <T> Future<T> submit(Callable<T> var1) {
      return this.next().submit(var1);
   }

   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      return this.next().schedule(var1, var2, var4);
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      return this.next().schedule(var1, var2, var4);
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      return this.next().scheduleAtFixedRate(var1, var2, var4, var6);
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      return this.next().scheduleWithFixedDelay(var1, var2, var4, var6);
   }

   public Future<?> shutdownGracefully() {
      return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
   }

   /** @deprecated */
   @Deprecated
   public abstract void shutdown();

   /** @deprecated */
   @Deprecated
   public List<Runnable> shutdownNow() {
      this.shutdown();
      return Collections.emptyList();
   }

   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
      return this.next().invokeAll(var1);
   }

   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.next().invokeAll(var1, var2, var4);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException {
      return this.next().invokeAny(var1);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException {
      return this.next().invokeAny(var1, var2, var4);
   }

   public void execute(Runnable var1) {
      this.next().execute(var1);
   }
}
