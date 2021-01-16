package io.netty.util.concurrent;

import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledEventExecutor extends AbstractEventExecutor {
   private static final Comparator<ScheduledFutureTask<?>> SCHEDULED_FUTURE_TASK_COMPARATOR = new Comparator<ScheduledFutureTask<?>>() {
      public int compare(ScheduledFutureTask<?> var1, ScheduledFutureTask<?> var2) {
         return var1.compareTo((Delayed)var2);
      }
   };
   PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue;

   protected AbstractScheduledEventExecutor() {
      super();
   }

   protected AbstractScheduledEventExecutor(EventExecutorGroup var1) {
      super(var1);
   }

   protected static long nanoTime() {
      return ScheduledFutureTask.nanoTime();
   }

   PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue() {
      if (this.scheduledTaskQueue == null) {
         this.scheduledTaskQueue = new DefaultPriorityQueue(SCHEDULED_FUTURE_TASK_COMPARATOR, 11);
      }

      return this.scheduledTaskQueue;
   }

   private static boolean isNullOrEmpty(Queue<ScheduledFutureTask<?>> var0) {
      return var0 == null || var0.isEmpty();
   }

   protected void cancelScheduledTasks() {
      assert this.inEventLoop();

      PriorityQueue var1 = this.scheduledTaskQueue;
      if (!isNullOrEmpty(var1)) {
         ScheduledFutureTask[] var2 = (ScheduledFutureTask[])var1.toArray(new ScheduledFutureTask[var1.size()]);
         ScheduledFutureTask[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ScheduledFutureTask var6 = var3[var5];
            var6.cancelWithoutRemove(false);
         }

         var1.clearIgnoringIndexes();
      }
   }

   protected final Runnable pollScheduledTask() {
      return this.pollScheduledTask(nanoTime());
   }

   protected final Runnable pollScheduledTask(long var1) {
      assert this.inEventLoop();

      PriorityQueue var3 = this.scheduledTaskQueue;
      ScheduledFutureTask var4 = var3 == null ? null : (ScheduledFutureTask)var3.peek();
      if (var4 == null) {
         return null;
      } else if (var4.deadlineNanos() <= var1) {
         var3.remove();
         return var4;
      } else {
         return null;
      }
   }

   protected final long nextScheduledTaskNano() {
      PriorityQueue var1 = this.scheduledTaskQueue;
      ScheduledFutureTask var2 = var1 == null ? null : (ScheduledFutureTask)var1.peek();
      return var2 == null ? -1L : Math.max(0L, var2.deadlineNanos() - nanoTime());
   }

   final ScheduledFutureTask<?> peekScheduledTask() {
      PriorityQueue var1 = this.scheduledTaskQueue;
      return var1 == null ? null : (ScheduledFutureTask)var1.peek();
   }

   protected final boolean hasScheduledTasks() {
      PriorityQueue var1 = this.scheduledTaskQueue;
      ScheduledFutureTask var2 = var1 == null ? null : (ScheduledFutureTask)var1.peek();
      return var2 != null && var2.deadlineNanos() <= nanoTime();
   }

   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      ObjectUtil.checkNotNull(var1, "command");
      ObjectUtil.checkNotNull(var4, "unit");
      if (var2 < 0L) {
         var2 = 0L;
      }

      this.validateScheduled(var2, var4);
      return this.schedule(new ScheduledFutureTask(this, var1, (Object)null, ScheduledFutureTask.deadlineNanos(var4.toNanos(var2))));
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      ObjectUtil.checkNotNull(var1, "callable");
      ObjectUtil.checkNotNull(var4, "unit");
      if (var2 < 0L) {
         var2 = 0L;
      }

      this.validateScheduled(var2, var4);
      return this.schedule(new ScheduledFutureTask(this, var1, ScheduledFutureTask.deadlineNanos(var4.toNanos(var2))));
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      ObjectUtil.checkNotNull(var1, "command");
      ObjectUtil.checkNotNull(var6, "unit");
      if (var2 < 0L) {
         throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", var2));
      } else if (var4 <= 0L) {
         throw new IllegalArgumentException(String.format("period: %d (expected: > 0)", var4));
      } else {
         this.validateScheduled(var2, var6);
         this.validateScheduled(var4, var6);
         return this.schedule(new ScheduledFutureTask(this, Executors.callable(var1, (Object)null), ScheduledFutureTask.deadlineNanos(var6.toNanos(var2)), var6.toNanos(var4)));
      }
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      ObjectUtil.checkNotNull(var1, "command");
      ObjectUtil.checkNotNull(var6, "unit");
      if (var2 < 0L) {
         throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", var2));
      } else if (var4 <= 0L) {
         throw new IllegalArgumentException(String.format("delay: %d (expected: > 0)", var4));
      } else {
         this.validateScheduled(var2, var6);
         this.validateScheduled(var4, var6);
         return this.schedule(new ScheduledFutureTask(this, Executors.callable(var1, (Object)null), ScheduledFutureTask.deadlineNanos(var6.toNanos(var2)), -var6.toNanos(var4)));
      }
   }

   protected void validateScheduled(long var1, TimeUnit var3) {
   }

   <V> ScheduledFuture<V> schedule(final ScheduledFutureTask<V> var1) {
      if (this.inEventLoop()) {
         this.scheduledTaskQueue().add(var1);
      } else {
         this.execute(new Runnable() {
            public void run() {
               AbstractScheduledEventExecutor.this.scheduledTaskQueue().add(var1);
            }
         });
      }

      return var1;
   }

   final void removeScheduled(final ScheduledFutureTask<?> var1) {
      if (this.inEventLoop()) {
         this.scheduledTaskQueue().removeTyped(var1);
      } else {
         this.execute(new Runnable() {
            public void run() {
               AbstractScheduledEventExecutor.this.removeScheduled(var1);
            }
         });
      }

   }
}
