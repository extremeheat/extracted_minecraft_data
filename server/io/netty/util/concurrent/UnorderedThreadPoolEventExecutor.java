package io.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class UnorderedThreadPoolEventExecutor extends ScheduledThreadPoolExecutor implements EventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnorderedThreadPoolEventExecutor.class);
   private final Promise<?> terminationFuture;
   private final Set<EventExecutor> executorSet;

   public UnorderedThreadPoolEventExecutor(int var1) {
      this(var1, (ThreadFactory)(new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class)));
   }

   public UnorderedThreadPoolEventExecutor(int var1, ThreadFactory var2) {
      super(var1, var2);
      this.terminationFuture = GlobalEventExecutor.INSTANCE.newPromise();
      this.executorSet = Collections.singleton(this);
   }

   public UnorderedThreadPoolEventExecutor(int var1, java.util.concurrent.RejectedExecutionHandler var2) {
      this(var1, new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class), var2);
   }

   public UnorderedThreadPoolEventExecutor(int var1, ThreadFactory var2, java.util.concurrent.RejectedExecutionHandler var3) {
      super(var1, var2, var3);
      this.terminationFuture = GlobalEventExecutor.INSTANCE.newPromise();
      this.executorSet = Collections.singleton(this);
   }

   public EventExecutor next() {
      return this;
   }

   public EventExecutorGroup parent() {
      return this;
   }

   public boolean inEventLoop() {
      return false;
   }

   public boolean inEventLoop(Thread var1) {
      return false;
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

   public boolean isShuttingDown() {
      return this.isShutdown();
   }

   public List<Runnable> shutdownNow() {
      List var1 = super.shutdownNow();
      this.terminationFuture.trySuccess((Object)null);
      return var1;
   }

   public void shutdown() {
      super.shutdown();
      this.terminationFuture.trySuccess((Object)null);
   }

   public Future<?> shutdownGracefully() {
      return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
   }

   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      this.shutdown();
      return this.terminationFuture();
   }

   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   public Iterator<EventExecutor> iterator() {
      return this.executorSet.iterator();
   }

   protected <V> RunnableScheduledFuture<V> decorateTask(Runnable var1, RunnableScheduledFuture<V> var2) {
      return (RunnableScheduledFuture)(var1 instanceof UnorderedThreadPoolEventExecutor.NonNotifyRunnable ? var2 : new UnorderedThreadPoolEventExecutor.RunnableScheduledFutureTask(this, var1, var2));
   }

   protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> var1, RunnableScheduledFuture<V> var2) {
      return new UnorderedThreadPoolEventExecutor.RunnableScheduledFutureTask(this, var1, var2);
   }

   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      return (ScheduledFuture)super.schedule(var1, var2, var4);
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      return (ScheduledFuture)super.schedule(var1, var2, var4);
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      return (ScheduledFuture)super.scheduleAtFixedRate(var1, var2, var4, var6);
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      return (ScheduledFuture)super.scheduleWithFixedDelay(var1, var2, var4, var6);
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

   public void execute(Runnable var1) {
      super.schedule(new UnorderedThreadPoolEventExecutor.NonNotifyRunnable(var1), 0L, TimeUnit.NANOSECONDS);
   }

   private static final class NonNotifyRunnable implements Runnable {
      private final Runnable task;

      NonNotifyRunnable(Runnable var1) {
         super();
         this.task = var1;
      }

      public void run() {
         this.task.run();
      }
   }

   private static final class RunnableScheduledFutureTask<V> extends PromiseTask<V> implements RunnableScheduledFuture<V>, ScheduledFuture<V> {
      private final RunnableScheduledFuture<V> future;

      RunnableScheduledFutureTask(EventExecutor var1, Runnable var2, RunnableScheduledFuture<V> var3) {
         super(var1, var2, (Object)null);
         this.future = var3;
      }

      RunnableScheduledFutureTask(EventExecutor var1, Callable<V> var2, RunnableScheduledFuture<V> var3) {
         super(var1, var2);
         this.future = var3;
      }

      public void run() {
         if (!this.isPeriodic()) {
            super.run();
         } else if (!this.isDone()) {
            try {
               this.task.call();
            } catch (Throwable var2) {
               if (!this.tryFailureInternal(var2)) {
                  UnorderedThreadPoolEventExecutor.logger.warn("Failure during execution of task", var2);
               }
            }
         }

      }

      public boolean isPeriodic() {
         return this.future.isPeriodic();
      }

      public long getDelay(TimeUnit var1) {
         return this.future.getDelay(var1);
      }

      public int compareTo(Delayed var1) {
         return this.future.compareTo(var1);
      }
   }
}
