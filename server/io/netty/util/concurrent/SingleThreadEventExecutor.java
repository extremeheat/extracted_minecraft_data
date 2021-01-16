package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class SingleThreadEventExecutor extends AbstractScheduledEventExecutor implements OrderedEventExecutor {
   static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Math.max(16, SystemPropertyUtil.getInt("io.netty.eventexecutor.maxPendingTasks", 2147483647));
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SingleThreadEventExecutor.class);
   private static final int ST_NOT_STARTED = 1;
   private static final int ST_STARTED = 2;
   private static final int ST_SHUTTING_DOWN = 3;
   private static final int ST_SHUTDOWN = 4;
   private static final int ST_TERMINATED = 5;
   private static final Runnable WAKEUP_TASK = new Runnable() {
      public void run() {
      }
   };
   private static final Runnable NOOP_TASK = new Runnable() {
      public void run() {
      }
   };
   private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");
   private static final AtomicReferenceFieldUpdater<SingleThreadEventExecutor, ThreadProperties> PROPERTIES_UPDATER = AtomicReferenceFieldUpdater.newUpdater(SingleThreadEventExecutor.class, ThreadProperties.class, "threadProperties");
   private final Queue<Runnable> taskQueue;
   private volatile Thread thread;
   private volatile ThreadProperties threadProperties;
   private final Executor executor;
   private volatile boolean interrupted;
   private final Semaphore threadLock;
   private final Set<Runnable> shutdownHooks;
   private final boolean addTaskWakesUp;
   private final int maxPendingTasks;
   private final RejectedExecutionHandler rejectedExecutionHandler;
   private long lastExecutionTime;
   private volatile int state;
   private volatile long gracefulShutdownQuietPeriod;
   private volatile long gracefulShutdownTimeout;
   private long gracefulShutdownStartTime;
   private final Promise<?> terminationFuture;
   private static final long SCHEDULE_PURGE_INTERVAL;

   protected SingleThreadEventExecutor(EventExecutorGroup var1, ThreadFactory var2, boolean var3) {
      this(var1, (Executor)(new ThreadPerTaskExecutor(var2)), var3);
   }

   protected SingleThreadEventExecutor(EventExecutorGroup var1, ThreadFactory var2, boolean var3, int var4, RejectedExecutionHandler var5) {
      this(var1, (Executor)(new ThreadPerTaskExecutor(var2)), var3, var4, var5);
   }

   protected SingleThreadEventExecutor(EventExecutorGroup var1, Executor var2, boolean var3) {
      this(var1, var2, var3, DEFAULT_MAX_PENDING_EXECUTOR_TASKS, RejectedExecutionHandlers.reject());
   }

   protected SingleThreadEventExecutor(EventExecutorGroup var1, Executor var2, boolean var3, int var4, RejectedExecutionHandler var5) {
      super(var1);
      this.threadLock = new Semaphore(0);
      this.shutdownHooks = new LinkedHashSet();
      this.state = 1;
      this.terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
      this.addTaskWakesUp = var3;
      this.maxPendingTasks = Math.max(16, var4);
      this.executor = (Executor)ObjectUtil.checkNotNull(var2, "executor");
      this.taskQueue = this.newTaskQueue(this.maxPendingTasks);
      this.rejectedExecutionHandler = (RejectedExecutionHandler)ObjectUtil.checkNotNull(var5, "rejectedHandler");
   }

   /** @deprecated */
   @Deprecated
   protected Queue<Runnable> newTaskQueue() {
      return this.newTaskQueue(this.maxPendingTasks);
   }

   protected Queue<Runnable> newTaskQueue(int var1) {
      return new LinkedBlockingQueue(var1);
   }

   protected void interruptThread() {
      Thread var1 = this.thread;
      if (var1 == null) {
         this.interrupted = true;
      } else {
         var1.interrupt();
      }

   }

   protected Runnable pollTask() {
      assert this.inEventLoop();

      return pollTaskFrom(this.taskQueue);
   }

   protected static Runnable pollTaskFrom(Queue<Runnable> var0) {
      Runnable var1;
      do {
         var1 = (Runnable)var0.poll();
      } while(var1 == WAKEUP_TASK);

      return var1;
   }

   protected Runnable takeTask() {
      assert this.inEventLoop();

      if (!(this.taskQueue instanceof BlockingQueue)) {
         throw new UnsupportedOperationException();
      } else {
         BlockingQueue var1 = (BlockingQueue)this.taskQueue;

         Runnable var5;
         do {
            ScheduledFutureTask var2 = this.peekScheduledTask();
            if (var2 == null) {
               Runnable var9 = null;

               try {
                  var9 = (Runnable)var1.take();
                  if (var9 == WAKEUP_TASK) {
                     var9 = null;
                  }
               } catch (InterruptedException var7) {
               }

               return var9;
            }

            long var3 = var2.delayNanos();
            var5 = null;
            if (var3 > 0L) {
               try {
                  var5 = (Runnable)var1.poll(var3, TimeUnit.NANOSECONDS);
               } catch (InterruptedException var8) {
                  return null;
               }
            }

            if (var5 == null) {
               this.fetchFromScheduledTaskQueue();
               var5 = (Runnable)var1.poll();
            }
         } while(var5 == null);

         return var5;
      }
   }

   private boolean fetchFromScheduledTaskQueue() {
      long var1 = AbstractScheduledEventExecutor.nanoTime();

      for(Runnable var3 = this.pollScheduledTask(var1); var3 != null; var3 = this.pollScheduledTask(var1)) {
         if (!this.taskQueue.offer(var3)) {
            this.scheduledTaskQueue().add((ScheduledFutureTask)var3);
            return false;
         }
      }

      return true;
   }

   protected Runnable peekTask() {
      assert this.inEventLoop();

      return (Runnable)this.taskQueue.peek();
   }

   protected boolean hasTasks() {
      assert this.inEventLoop();

      return !this.taskQueue.isEmpty();
   }

   public int pendingTasks() {
      return this.taskQueue.size();
   }

   protected void addTask(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException("task");
      } else {
         if (!this.offerTask(var1)) {
            this.reject(var1);
         }

      }
   }

   final boolean offerTask(Runnable var1) {
      if (this.isShutdown()) {
         reject();
      }

      return this.taskQueue.offer(var1);
   }

   protected boolean removeTask(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException("task");
      } else {
         return this.taskQueue.remove(var1);
      }
   }

   protected boolean runAllTasks() {
      assert this.inEventLoop();

      boolean var2 = false;

      boolean var1;
      do {
         var1 = this.fetchFromScheduledTaskQueue();
         if (this.runAllTasksFrom(this.taskQueue)) {
            var2 = true;
         }
      } while(!var1);

      if (var2) {
         this.lastExecutionTime = ScheduledFutureTask.nanoTime();
      }

      this.afterRunningAllTasks();
      return var2;
   }

   protected final boolean runAllTasksFrom(Queue<Runnable> var1) {
      Runnable var2 = pollTaskFrom(var1);
      if (var2 == null) {
         return false;
      } else {
         do {
            safeExecute(var2);
            var2 = pollTaskFrom(var1);
         } while(var2 != null);

         return true;
      }
   }

   protected boolean runAllTasks(long var1) {
      this.fetchFromScheduledTaskQueue();
      Runnable var3 = this.pollTask();
      if (var3 == null) {
         this.afterRunningAllTasks();
         return false;
      } else {
         long var4 = ScheduledFutureTask.nanoTime() + var1;
         long var6 = 0L;

         long var8;
         while(true) {
            safeExecute(var3);
            ++var6;
            if ((var6 & 63L) == 0L) {
               var8 = ScheduledFutureTask.nanoTime();
               if (var8 >= var4) {
                  break;
               }
            }

            var3 = this.pollTask();
            if (var3 == null) {
               var8 = ScheduledFutureTask.nanoTime();
               break;
            }
         }

         this.afterRunningAllTasks();
         this.lastExecutionTime = var8;
         return true;
      }
   }

   protected void afterRunningAllTasks() {
   }

   protected long delayNanos(long var1) {
      ScheduledFutureTask var3 = this.peekScheduledTask();
      return var3 == null ? SCHEDULE_PURGE_INTERVAL : var3.delayNanos(var1);
   }

   protected void updateLastExecutionTime() {
      this.lastExecutionTime = ScheduledFutureTask.nanoTime();
   }

   protected abstract void run();

   protected void cleanup() {
   }

   protected void wakeup(boolean var1) {
      if (!var1 || this.state == 3) {
         this.taskQueue.offer(WAKEUP_TASK);
      }

   }

   public boolean inEventLoop(Thread var1) {
      return var1 == this.thread;
   }

   public void addShutdownHook(final Runnable var1) {
      if (this.inEventLoop()) {
         this.shutdownHooks.add(var1);
      } else {
         this.execute(new Runnable() {
            public void run() {
               SingleThreadEventExecutor.this.shutdownHooks.add(var1);
            }
         });
      }

   }

   public void removeShutdownHook(final Runnable var1) {
      if (this.inEventLoop()) {
         this.shutdownHooks.remove(var1);
      } else {
         this.execute(new Runnable() {
            public void run() {
               SingleThreadEventExecutor.this.shutdownHooks.remove(var1);
            }
         });
      }

   }

   private boolean runShutdownHooks() {
      boolean var1 = false;

      while(!this.shutdownHooks.isEmpty()) {
         ArrayList var2 = new ArrayList(this.shutdownHooks);
         this.shutdownHooks.clear();
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Runnable var4 = (Runnable)var3.next();

            try {
               var4.run();
            } catch (Throwable var9) {
               logger.warn("Shutdown hook raised an exception.", var9);
            } finally {
               var1 = true;
            }
         }
      }

      if (var1) {
         this.lastExecutionTime = ScheduledFutureTask.nanoTime();
      }

      return var1;
   }

   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("quietPeriod: " + var1 + " (expected >= 0)");
      } else if (var3 < var1) {
         throw new IllegalArgumentException("timeout: " + var3 + " (expected >= quietPeriod (" + var1 + "))");
      } else if (var5 == null) {
         throw new NullPointerException("unit");
      } else if (this.isShuttingDown()) {
         return this.terminationFuture();
      } else {
         boolean var6 = this.inEventLoop();

         while(!this.isShuttingDown()) {
            boolean var7 = true;
            int var8 = this.state;
            int var9;
            if (var6) {
               var9 = 3;
            } else {
               switch(var8) {
               case 1:
               case 2:
                  var9 = 3;
                  break;
               default:
                  var9 = var8;
                  var7 = false;
               }
            }

            if (STATE_UPDATER.compareAndSet(this, var8, var9)) {
               this.gracefulShutdownQuietPeriod = var5.toNanos(var1);
               this.gracefulShutdownTimeout = var5.toNanos(var3);
               if (var8 == 1) {
                  try {
                     this.doStartThread();
                  } catch (Throwable var10) {
                     STATE_UPDATER.set(this, 5);
                     this.terminationFuture.tryFailure(var10);
                     if (!(var10 instanceof Exception)) {
                        PlatformDependent.throwException(var10);
                     }

                     return this.terminationFuture;
                  }
               }

               if (var7) {
                  this.wakeup(var6);
               }

               return this.terminationFuture();
            }
         }

         return this.terminationFuture();
      }
   }

   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   /** @deprecated */
   @Deprecated
   public void shutdown() {
      if (!this.isShutdown()) {
         boolean var1 = this.inEventLoop();

         while(!this.isShuttingDown()) {
            boolean var2 = true;
            int var3 = this.state;
            int var4;
            if (var1) {
               var4 = 4;
            } else {
               switch(var3) {
               case 1:
               case 2:
               case 3:
                  var4 = 4;
                  break;
               default:
                  var4 = var3;
                  var2 = false;
               }
            }

            if (STATE_UPDATER.compareAndSet(this, var3, var4)) {
               if (var3 == 1) {
                  try {
                     this.doStartThread();
                  } catch (Throwable var5) {
                     STATE_UPDATER.set(this, 5);
                     this.terminationFuture.tryFailure(var5);
                     if (!(var5 instanceof Exception)) {
                        PlatformDependent.throwException(var5);
                     }

                     return;
                  }
               }

               if (var2) {
                  this.wakeup(var1);
               }

               return;
            }
         }

      }
   }

   public boolean isShuttingDown() {
      return this.state >= 3;
   }

   public boolean isShutdown() {
      return this.state >= 4;
   }

   public boolean isTerminated() {
      return this.state == 5;
   }

   protected boolean confirmShutdown() {
      if (!this.isShuttingDown()) {
         return false;
      } else if (!this.inEventLoop()) {
         throw new IllegalStateException("must be invoked from an event loop");
      } else {
         this.cancelScheduledTasks();
         if (this.gracefulShutdownStartTime == 0L) {
            this.gracefulShutdownStartTime = ScheduledFutureTask.nanoTime();
         }

         if (!this.runAllTasks() && !this.runShutdownHooks()) {
            long var1 = ScheduledFutureTask.nanoTime();
            if (!this.isShutdown() && var1 - this.gracefulShutdownStartTime <= this.gracefulShutdownTimeout) {
               if (var1 - this.lastExecutionTime <= this.gracefulShutdownQuietPeriod) {
                  this.wakeup(true);

                  try {
                     Thread.sleep(100L);
                  } catch (InterruptedException var4) {
                  }

                  return false;
               } else {
                  return true;
               }
            } else {
               return true;
            }
         } else if (this.isShutdown()) {
            return true;
         } else if (this.gracefulShutdownQuietPeriod == 0L) {
            return true;
         } else {
            this.wakeup(true);
            return false;
         }
      }
   }

   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      if (var3 == null) {
         throw new NullPointerException("unit");
      } else if (this.inEventLoop()) {
         throw new IllegalStateException("cannot await termination of the current thread");
      } else {
         if (this.threadLock.tryAcquire(var1, var3)) {
            this.threadLock.release();
         }

         return this.isTerminated();
      }
   }

   public void execute(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException("task");
      } else {
         boolean var2 = this.inEventLoop();
         this.addTask(var1);
         if (!var2) {
            this.startThread();
            if (this.isShutdown() && this.removeTask(var1)) {
               reject();
            }
         }

         if (!this.addTaskWakesUp && this.wakesUpForTask(var1)) {
            this.wakeup(var2);
         }

      }
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException {
      this.throwIfInEventLoop("invokeAny");
      return super.invokeAny(var1);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException {
      this.throwIfInEventLoop("invokeAny");
      return super.invokeAny(var1, var2, var4);
   }

   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
      this.throwIfInEventLoop("invokeAll");
      return super.invokeAll(var1);
   }

   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
      this.throwIfInEventLoop("invokeAll");
      return super.invokeAll(var1, var2, var4);
   }

   private void throwIfInEventLoop(String var1) {
      if (this.inEventLoop()) {
         throw new RejectedExecutionException("Calling " + var1 + " from within the EventLoop is not allowed");
      }
   }

   public final ThreadProperties threadProperties() {
      Object var1 = this.threadProperties;
      if (var1 == null) {
         Thread var2 = this.thread;
         if (var2 == null) {
            assert !this.inEventLoop();

            this.submit(NOOP_TASK).syncUninterruptibly();
            var2 = this.thread;

            assert var2 != null;
         }

         var1 = new SingleThreadEventExecutor.DefaultThreadProperties(var2);
         if (!PROPERTIES_UPDATER.compareAndSet(this, (Object)null, var1)) {
            var1 = this.threadProperties;
         }
      }

      return (ThreadProperties)var1;
   }

   protected boolean wakesUpForTask(Runnable var1) {
      return true;
   }

   protected static void reject() {
      throw new RejectedExecutionException("event executor terminated");
   }

   protected final void reject(Runnable var1) {
      this.rejectedExecutionHandler.rejected(var1, this);
   }

   private void startThread() {
      if (this.state == 1 && STATE_UPDATER.compareAndSet(this, 1, 2)) {
         try {
            this.doStartThread();
         } catch (Throwable var2) {
            STATE_UPDATER.set(this, 1);
            PlatformDependent.throwException(var2);
         }
      }

   }

   private void doStartThread() {
      assert this.thread == null;

      this.executor.execute(new Runnable() {
         public void run() {
            SingleThreadEventExecutor.this.thread = Thread.currentThread();
            if (SingleThreadEventExecutor.this.interrupted) {
               SingleThreadEventExecutor.this.thread.interrupt();
            }

            boolean var1 = false;
            SingleThreadEventExecutor.this.updateLastExecutionTime();
            boolean var112 = false;

            int var2;
            label1685: {
               try {
                  var112 = true;
                  SingleThreadEventExecutor.this.run();
                  var1 = true;
                  var112 = false;
                  break label1685;
               } catch (Throwable var119) {
                  SingleThreadEventExecutor.logger.warn("Unexpected exception from an event executor: ", var119);
                  var112 = false;
               } finally {
                  if (var112) {
                     int var10;
                     do {
                        var10 = SingleThreadEventExecutor.this.state;
                     } while(var10 < 3 && !SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, var10, 3));

                     if (var1 && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L) {
                        SingleThreadEventExecutor.logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called before run() implementation terminates.");
                     }

                     try {
                        while(!SingleThreadEventExecutor.this.confirmShutdown()) {
                        }
                     } finally {
                        try {
                           SingleThreadEventExecutor.this.cleanup();
                        } finally {
                           SingleThreadEventExecutor.STATE_UPDATER.set(SingleThreadEventExecutor.this, 5);
                           SingleThreadEventExecutor.this.threadLock.release();
                           if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                              SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                           }

                           SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object)null);
                        }
                     }

                  }
               }

               do {
                  var2 = SingleThreadEventExecutor.this.state;
               } while(var2 < 3 && !SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, var2, 3));

               if (var1 && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L) {
                  SingleThreadEventExecutor.logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called before run() implementation terminates.");
               }

               try {
                  while(!SingleThreadEventExecutor.this.confirmShutdown()) {
                  }

                  return;
               } finally {
                  try {
                     SingleThreadEventExecutor.this.cleanup();
                  } finally {
                     SingleThreadEventExecutor.STATE_UPDATER.set(SingleThreadEventExecutor.this, 5);
                     SingleThreadEventExecutor.this.threadLock.release();
                     if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                        SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                     }

                     SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object)null);
                  }
               }
            }

            do {
               var2 = SingleThreadEventExecutor.this.state;
            } while(var2 < 3 && !SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, var2, 3));

            if (var1 && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L) {
               SingleThreadEventExecutor.logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called before run() implementation terminates.");
            }

            try {
               while(!SingleThreadEventExecutor.this.confirmShutdown()) {
               }
            } finally {
               try {
                  SingleThreadEventExecutor.this.cleanup();
               } finally {
                  SingleThreadEventExecutor.STATE_UPDATER.set(SingleThreadEventExecutor.this, 5);
                  SingleThreadEventExecutor.this.threadLock.release();
                  if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                     SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                  }

                  SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object)null);
               }
            }

         }
      });
   }

   static {
      SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1L);
   }

   private static final class DefaultThreadProperties implements ThreadProperties {
      private final Thread t;

      DefaultThreadProperties(Thread var1) {
         super();
         this.t = var1;
      }

      public State state() {
         return this.t.getState();
      }

      public int priority() {
         return this.t.getPriority();
      }

      public boolean isInterrupted() {
         return this.t.isInterrupted();
      }

      public boolean isDaemon() {
         return this.t.isDaemon();
      }

      public String name() {
         return this.t.getName();
      }

      public long id() {
         return this.t.getId();
      }

      public StackTraceElement[] stackTrace() {
         return this.t.getStackTrace();
      }

      public boolean isAlive() {
         return this.t.isAlive();
      }
   }
}
