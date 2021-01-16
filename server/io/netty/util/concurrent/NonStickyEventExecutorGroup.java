package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public final class NonStickyEventExecutorGroup implements EventExecutorGroup {
   private final EventExecutorGroup group;
   private final int maxTaskExecutePerRun;

   public NonStickyEventExecutorGroup(EventExecutorGroup var1) {
      this(var1, 1024);
   }

   public NonStickyEventExecutorGroup(EventExecutorGroup var1, int var2) {
      super();
      this.group = verify(var1);
      this.maxTaskExecutePerRun = ObjectUtil.checkPositive(var2, "maxTaskExecutePerRun");
   }

   private static EventExecutorGroup verify(EventExecutorGroup var0) {
      Iterator var1 = ((EventExecutorGroup)ObjectUtil.checkNotNull(var0, "group")).iterator();

      EventExecutor var2;
      do {
         if (!var1.hasNext()) {
            return var0;
         }

         var2 = (EventExecutor)var1.next();
      } while(!(var2 instanceof OrderedEventExecutor));

      throw new IllegalArgumentException("EventExecutorGroup " + var0 + " contains OrderedEventExecutors: " + var2);
   }

   private NonStickyEventExecutorGroup.NonStickyOrderedEventExecutor newExecutor(EventExecutor var1) {
      return new NonStickyEventExecutorGroup.NonStickyOrderedEventExecutor(var1, this.maxTaskExecutePerRun);
   }

   public boolean isShuttingDown() {
      return this.group.isShuttingDown();
   }

   public Future<?> shutdownGracefully() {
      return this.group.shutdownGracefully();
   }

   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      return this.group.shutdownGracefully(var1, var3, var5);
   }

   public Future<?> terminationFuture() {
      return this.group.terminationFuture();
   }

   public void shutdown() {
      this.group.shutdown();
   }

   public List<Runnable> shutdownNow() {
      return this.group.shutdownNow();
   }

   public EventExecutor next() {
      return this.newExecutor(this.group.next());
   }

   public Iterator<EventExecutor> iterator() {
      final Iterator var1 = this.group.iterator();
      return new Iterator<EventExecutor>() {
         public boolean hasNext() {
            return var1.hasNext();
         }

         public EventExecutor next() {
            return NonStickyEventExecutorGroup.this.newExecutor((EventExecutor)var1.next());
         }

         public void remove() {
            var1.remove();
         }
      };
   }

   public Future<?> submit(Runnable var1) {
      return this.group.submit(var1);
   }

   public <T> Future<T> submit(Runnable var1, T var2) {
      return this.group.submit(var1, var2);
   }

   public <T> Future<T> submit(Callable<T> var1) {
      return this.group.submit(var1);
   }

   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      return this.group.schedule(var1, var2, var4);
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      return this.group.schedule(var1, var2, var4);
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      return this.group.scheduleAtFixedRate(var1, var2, var4, var6);
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      return this.group.scheduleWithFixedDelay(var1, var2, var4, var6);
   }

   public boolean isShutdown() {
      return this.group.isShutdown();
   }

   public boolean isTerminated() {
      return this.group.isTerminated();
   }

   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      return this.group.awaitTermination(var1, var3);
   }

   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
      return this.group.invokeAll(var1);
   }

   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.group.invokeAll(var1, var2, var4);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException {
      return this.group.invokeAny(var1);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException {
      return this.group.invokeAny(var1, var2, var4);
   }

   public void execute(Runnable var1) {
      this.group.execute(var1);
   }

   private static final class NonStickyOrderedEventExecutor extends AbstractEventExecutor implements Runnable, OrderedEventExecutor {
      private final EventExecutor executor;
      private final Queue<Runnable> tasks = PlatformDependent.newMpscQueue();
      private static final int NONE = 0;
      private static final int SUBMITTED = 1;
      private static final int RUNNING = 2;
      private final AtomicInteger state = new AtomicInteger();
      private final int maxTaskExecutePerRun;

      NonStickyOrderedEventExecutor(EventExecutor var1, int var2) {
         super(var1);
         this.executor = var1;
         this.maxTaskExecutePerRun = var2;
      }

      public void run() {
         if (this.state.compareAndSet(1, 2)) {
            while(true) {
               int var1 = 0;

               try {
                  while(var1 < this.maxTaskExecutePerRun) {
                     Runnable var2 = (Runnable)this.tasks.poll();
                     if (var2 == null) {
                        break;
                     }

                     safeExecute(var2);
                     ++var1;
                  }
               } finally {
                  if (var1 == this.maxTaskExecutePerRun) {
                     try {
                        this.state.set(1);
                        this.executor.execute(this);
                        return;
                     } catch (Throwable var8) {
                        this.state.set(2);
                     }
                  }

                  this.state.set(0);
                  return;
               }
            }
         }
      }

      public boolean inEventLoop(Thread var1) {
         return false;
      }

      public boolean inEventLoop() {
         return false;
      }

      public boolean isShuttingDown() {
         return this.executor.isShutdown();
      }

      public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
         return this.executor.shutdownGracefully(var1, var3, var5);
      }

      public Future<?> terminationFuture() {
         return this.executor.terminationFuture();
      }

      public void shutdown() {
         this.executor.shutdown();
      }

      public boolean isShutdown() {
         return this.executor.isShutdown();
      }

      public boolean isTerminated() {
         return this.executor.isTerminated();
      }

      public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
         return this.executor.awaitTermination(var1, var3);
      }

      public void execute(Runnable var1) {
         if (!this.tasks.offer(var1)) {
            throw new RejectedExecutionException();
         } else {
            if (this.state.compareAndSet(0, 1)) {
               try {
                  this.executor.execute(this);
               } catch (Throwable var3) {
                  this.tasks.remove(var1);
                  PlatformDependent.throwException(var3);
               }
            }

         }
      }
   }
}
