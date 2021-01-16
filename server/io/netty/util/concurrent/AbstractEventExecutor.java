package io.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractEventExecutor extends AbstractExecutorService implements EventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractEventExecutor.class);
   static final long DEFAULT_SHUTDOWN_QUIET_PERIOD = 2L;
   static final long DEFAULT_SHUTDOWN_TIMEOUT = 15L;
   private final EventExecutorGroup parent;
   private final Collection<EventExecutor> selfCollection;

   protected AbstractEventExecutor() {
      this((EventExecutorGroup)null);
   }

   protected AbstractEventExecutor(EventExecutorGroup var1) {
      super();
      this.selfCollection = Collections.singleton(this);
      this.parent = var1;
   }

   public EventExecutorGroup parent() {
      return this.parent;
   }

   public EventExecutor next() {
      return this;
   }

   public boolean inEventLoop() {
      return this.inEventLoop(Thread.currentThread());
   }

   public Iterator<EventExecutor> iterator() {
      return this.selfCollection.iterator();
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

   public <V> Promise<V> newPromise() {
      return new DefaultPromise(this);
   }

   public <V> ProgressivePromise<V> newProgressivePromise() {
      return new DefaultProgressivePromise(this);
   }

   public <V> Future<V> newSucceededFuture(V var1) {
      return new SucceededFuture(this, var1);
   }

   public <V> Future<V> newFailedFuture(Throwable var1) {
      return new FailedFuture(this, var1);
   }

   public Future<?> submit(Runnable var1) {
      return (Future)super.submit(var1);
   }

   public <T> Future<T> submit(Runnable var1, T var2) {
      return (Future)super.submit(var1, var2);
   }

   public <T> Future<T> submit(Callable<T> var1) {
      return (Future)super.submit(var1);
   }

   protected final <T> RunnableFuture<T> newTaskFor(Runnable var1, T var2) {
      return new PromiseTask(this, var1, var2);
   }

   protected final <T> RunnableFuture<T> newTaskFor(Callable<T> var1) {
      return new PromiseTask(this, var1);
   }

   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      throw new UnsupportedOperationException();
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      throw new UnsupportedOperationException();
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      throw new UnsupportedOperationException();
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      throw new UnsupportedOperationException();
   }

   protected static void safeExecute(Runnable var0) {
      try {
         var0.run();
      } catch (Throwable var2) {
         logger.warn("A task raised an exception. Task: {}", var0, var2);
      }

   }
}
