package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public interface StrictQueue<T, F> {
   @Nullable
   F pop();

   boolean push(T var1);

   boolean isEmpty();

   int size();

   public static final class FixedPriorityQueue implements StrictQueue<StrictQueue.IntRunnable, Runnable> {
      private final Queue<Runnable>[] queues;
      private final AtomicInteger size = new AtomicInteger();

      public FixedPriorityQueue(int var1) {
         super();
         this.queues = new Queue[var1];

         for (int var2 = 0; var2 < var1; var2++) {
            this.queues[var2] = Queues.newConcurrentLinkedQueue();
         }
      }

      @Nullable
      public Runnable pop() {
         for (Queue var4 : this.queues) {
            Runnable var5 = (Runnable)var4.poll();
            if (var5 != null) {
               this.size.decrementAndGet();
               return var5;
            }
         }

         return null;
      }

      public boolean push(StrictQueue.IntRunnable var1) {
         int var2 = var1.priority;
         if (var2 < this.queues.length && var2 >= 0) {
            this.queues[var2].add(var1);
            this.size.incrementAndGet();
            return true;
         } else {
            throw new IndexOutOfBoundsException(String.format(Locale.ROOT, "Priority %d not supported. Expected range [0-%d]", var2, this.queues.length - 1));
         }
      }

      @Override
      public boolean isEmpty() {
         return this.size.get() == 0;
      }

      @Override
      public int size() {
         return this.size.get();
      }
   }

   public static final class IntRunnable implements Runnable {
      final int priority;
      private final Runnable task;

      public IntRunnable(int var1, Runnable var2) {
         super();
         this.priority = var1;
         this.task = var2;
      }

      @Override
      public void run() {
         this.task.run();
      }

      public int getPriority() {
         return this.priority;
      }
   }

   public static final class QueueStrictQueue<T> implements StrictQueue<T, T> {
      private final Queue<T> queue;

      public QueueStrictQueue(Queue<T> var1) {
         super();
         this.queue = var1;
      }

      @Nullable
      @Override
      public T pop() {
         return this.queue.poll();
      }

      @Override
      public boolean push(T var1) {
         return this.queue.add((T)var1);
      }

      @Override
      public boolean isEmpty() {
         return this.queue.isEmpty();
      }

      @Override
      public int size() {
         return this.queue.size();
      }
   }
}