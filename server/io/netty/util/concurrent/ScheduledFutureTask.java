package io.netty.util.concurrent;

import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.PriorityQueueNode;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

final class ScheduledFutureTask<V> extends PromiseTask<V> implements ScheduledFuture<V>, PriorityQueueNode {
   private static final AtomicLong nextTaskId = new AtomicLong();
   private static final long START_TIME = System.nanoTime();
   private final long id;
   private long deadlineNanos;
   private final long periodNanos;
   private int queueIndex;

   static long nanoTime() {
      return System.nanoTime() - START_TIME;
   }

   static long deadlineNanos(long var0) {
      return nanoTime() + var0;
   }

   ScheduledFutureTask(AbstractScheduledEventExecutor var1, Runnable var2, V var3, long var4) {
      this(var1, toCallable(var2, var3), var4);
   }

   ScheduledFutureTask(AbstractScheduledEventExecutor var1, Callable<V> var2, long var3, long var5) {
      super(var1, var2);
      this.id = nextTaskId.getAndIncrement();
      this.queueIndex = -1;
      if (var5 == 0L) {
         throw new IllegalArgumentException("period: 0 (expected: != 0)");
      } else {
         this.deadlineNanos = var3;
         this.periodNanos = var5;
      }
   }

   ScheduledFutureTask(AbstractScheduledEventExecutor var1, Callable<V> var2, long var3) {
      super(var1, var2);
      this.id = nextTaskId.getAndIncrement();
      this.queueIndex = -1;
      this.deadlineNanos = var3;
      this.periodNanos = 0L;
   }

   protected EventExecutor executor() {
      return super.executor();
   }

   public long deadlineNanos() {
      return this.deadlineNanos;
   }

   public long delayNanos() {
      return Math.max(0L, this.deadlineNanos() - nanoTime());
   }

   public long delayNanos(long var1) {
      return Math.max(0L, this.deadlineNanos() - (var1 - START_TIME));
   }

   public long getDelay(TimeUnit var1) {
      return var1.convert(this.delayNanos(), TimeUnit.NANOSECONDS);
   }

   public int compareTo(Delayed var1) {
      if (this == var1) {
         return 0;
      } else {
         ScheduledFutureTask var2 = (ScheduledFutureTask)var1;
         long var3 = this.deadlineNanos() - var2.deadlineNanos();
         if (var3 < 0L) {
            return -1;
         } else if (var3 > 0L) {
            return 1;
         } else if (this.id < var2.id) {
            return -1;
         } else if (this.id == var2.id) {
            throw new Error();
         } else {
            return 1;
         }
      }
   }

   public void run() {
      assert this.executor().inEventLoop();

      try {
         if (this.periodNanos == 0L) {
            if (this.setUncancellableInternal()) {
               Object var1 = this.task.call();
               this.setSuccessInternal(var1);
            }
         } else if (!this.isCancelled()) {
            this.task.call();
            if (!this.executor().isShutdown()) {
               long var5 = this.periodNanos;
               if (var5 > 0L) {
                  this.deadlineNanos += var5;
               } else {
                  this.deadlineNanos = nanoTime() - var5;
               }

               if (!this.isCancelled()) {
                  PriorityQueue var3 = ((AbstractScheduledEventExecutor)this.executor()).scheduledTaskQueue;

                  assert var3 != null;

                  var3.add(this);
               }
            }
         }
      } catch (Throwable var4) {
         this.setFailureInternal(var4);
      }

   }

   public boolean cancel(boolean var1) {
      boolean var2 = super.cancel(var1);
      if (var2) {
         ((AbstractScheduledEventExecutor)this.executor()).removeScheduled(this);
      }

      return var2;
   }

   boolean cancelWithoutRemove(boolean var1) {
      return super.cancel(var1);
   }

   protected StringBuilder toStringBuilder() {
      StringBuilder var1 = super.toStringBuilder();
      var1.setCharAt(var1.length() - 1, ',');
      return var1.append(" id: ").append(this.id).append(", deadline: ").append(this.deadlineNanos).append(", period: ").append(this.periodNanos).append(')');
   }

   public int priorityQueueIndex(DefaultPriorityQueue<?> var1) {
      return this.queueIndex;
   }

   public void priorityQueueIndex(DefaultPriorityQueue<?> var1, int var2) {
      this.queueIndex = var2;
   }
}
