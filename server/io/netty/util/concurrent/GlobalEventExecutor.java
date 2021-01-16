package io.netty.util.concurrent;

import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GlobalEventExecutor extends AbstractScheduledEventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(GlobalEventExecutor.class);
   private static final long SCHEDULE_QUIET_PERIOD_INTERVAL;
   public static final GlobalEventExecutor INSTANCE;
   final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue();
   final ScheduledFutureTask<Void> quietPeriodTask;
   final ThreadFactory threadFactory;
   private final GlobalEventExecutor.TaskRunner taskRunner;
   private final AtomicBoolean started;
   volatile Thread thread;
   private final Future<?> terminationFuture;

   private GlobalEventExecutor() {
      super();
      this.quietPeriodTask = new ScheduledFutureTask(this, Executors.callable(new Runnable() {
         public void run() {
         }
      }, (Object)null), ScheduledFutureTask.deadlineNanos(SCHEDULE_QUIET_PERIOD_INTERVAL), -SCHEDULE_QUIET_PERIOD_INTERVAL);
      this.threadFactory = new DefaultThreadFactory(DefaultThreadFactory.toPoolName(this.getClass()), false, 5, (ThreadGroup)null);
      this.taskRunner = new GlobalEventExecutor.TaskRunner();
      this.started = new AtomicBoolean();
      this.terminationFuture = new FailedFuture(this, new UnsupportedOperationException());
      this.scheduledTaskQueue().add(this.quietPeriodTask);
   }

   Runnable takeTask() {
      BlockingQueue var1 = this.taskQueue;

      Runnable var5;
      do {
         ScheduledFutureTask var2 = this.peekScheduledTask();
         if (var2 == null) {
            Runnable var9 = null;

            try {
               var9 = (Runnable)var1.take();
            } catch (InterruptedException var7) {
            }

            return var9;
         }

         long var3 = var2.delayNanos();
         if (var3 > 0L) {
            try {
               var5 = (Runnable)var1.poll(var3, TimeUnit.NANOSECONDS);
            } catch (InterruptedException var8) {
               return null;
            }
         } else {
            var5 = (Runnable)var1.poll();
         }

         if (var5 == null) {
            this.fetchFromScheduledTaskQueue();
            var5 = (Runnable)var1.poll();
         }
      } while(var5 == null);

      return var5;
   }

   private void fetchFromScheduledTaskQueue() {
      long var1 = AbstractScheduledEventExecutor.nanoTime();

      for(Runnable var3 = this.pollScheduledTask(var1); var3 != null; var3 = this.pollScheduledTask(var1)) {
         this.taskQueue.add(var3);
      }

   }

   public int pendingTasks() {
      return this.taskQueue.size();
   }

   private void addTask(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException("task");
      } else {
         this.taskQueue.add(var1);
      }
   }

   public boolean inEventLoop(Thread var1) {
      return var1 == this.thread;
   }

   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      return this.terminationFuture();
   }

   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   /** @deprecated */
   @Deprecated
   public void shutdown() {
      throw new UnsupportedOperationException();
   }

   public boolean isShuttingDown() {
      return false;
   }

   public boolean isShutdown() {
      return false;
   }

   public boolean isTerminated() {
      return false;
   }

   public boolean awaitTermination(long var1, TimeUnit var3) {
      return false;
   }

   public boolean awaitInactivity(long var1, TimeUnit var3) throws InterruptedException {
      if (var3 == null) {
         throw new NullPointerException("unit");
      } else {
         Thread var4 = this.thread;
         if (var4 == null) {
            throw new IllegalStateException("thread was not started");
         } else {
            var4.join(var3.toMillis(var1));
            return !var4.isAlive();
         }
      }
   }

   public void execute(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException("task");
      } else {
         this.addTask(var1);
         if (!this.inEventLoop()) {
            this.startThread();
         }

      }
   }

   private void startThread() {
      if (this.started.compareAndSet(false, true)) {
         final Thread var1 = this.threadFactory.newThread(this.taskRunner);
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               var1.setContextClassLoader((ClassLoader)null);
               return null;
            }
         });
         this.thread = var1;
         var1.start();
      }

   }

   static {
      SCHEDULE_QUIET_PERIOD_INTERVAL = TimeUnit.SECONDS.toNanos(1L);
      INSTANCE = new GlobalEventExecutor();
   }

   final class TaskRunner implements Runnable {
      TaskRunner() {
         super();
      }

      public void run() {
         while(true) {
            Runnable var1 = GlobalEventExecutor.this.takeTask();
            if (var1 != null) {
               try {
                  var1.run();
               } catch (Throwable var4) {
                  GlobalEventExecutor.logger.warn("Unexpected exception from the global event executor: ", var4);
               }

               if (var1 != GlobalEventExecutor.this.quietPeriodTask) {
                  continue;
               }
            }

            PriorityQueue var2 = GlobalEventExecutor.this.scheduledTaskQueue;
            if (GlobalEventExecutor.this.taskQueue.isEmpty() && (var2 == null || var2.size() == 1)) {
               boolean var3 = GlobalEventExecutor.this.started.compareAndSet(true, false);

               assert var3;

               if (GlobalEventExecutor.this.taskQueue.isEmpty() && (var2 == null || var2.size() == 1) || !GlobalEventExecutor.this.started.compareAndSet(false, true)) {
                  return;
               }
            }
         }
      }
   }
}
