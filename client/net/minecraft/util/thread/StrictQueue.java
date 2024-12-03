package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public interface StrictQueue<T extends Runnable> {
   @Nullable
   Runnable pop();

   boolean push(T var1);

   boolean isEmpty();

   int size();

   public static final class QueueStrictQueue implements StrictQueue<Runnable> {
      private final Queue<Runnable> queue;

      public QueueStrictQueue(Queue<Runnable> var1) {
         super();
         this.queue = var1;
      }

      @Nullable
      public Runnable pop() {
         return (Runnable)this.queue.poll();
      }

      public boolean push(Runnable var1) {
         return this.queue.add(var1);
      }

      public boolean isEmpty() {
         return this.queue.isEmpty();
      }

      public int size() {
         return this.queue.size();
      }
   }

   public static record RunnableWithPriority(int priority, Runnable task) implements Runnable {
      final int priority;

      public RunnableWithPriority(int var1, Runnable var2) {
         super();
         this.priority = var1;
         this.task = var2;
      }

      public void run() {
         this.task.run();
      }
   }

   public static final class FixedPriorityQueue implements StrictQueue<RunnableWithPriority> {
      private final Queue<Runnable>[] queues;
      private final AtomicInteger size = new AtomicInteger();

      public FixedPriorityQueue(int var1) {
         super();
         this.queues = new Queue[var1];

         for(int var2 = 0; var2 < var1; ++var2) {
            this.queues[var2] = Queues.newConcurrentLinkedQueue();
         }

      }

      @Nullable
      public Runnable pop() {
         for(Queue var4 : this.queues) {
            Runnable var5 = (Runnable)var4.poll();
            if (var5 != null) {
               this.size.decrementAndGet();
               return var5;
            }
         }

         return null;
      }

      public boolean push(RunnableWithPriority var1) {
         int var2 = var1.priority;
         if (var2 < this.queues.length && var2 >= 0) {
            this.queues[var2].add(var1);
            this.size.incrementAndGet();
            return true;
         } else {
            throw new IndexOutOfBoundsException(String.format(Locale.ROOT, "Priority %d not supported. Expected range [0-%d]", var2, this.queues.length - 1));
         }
      }

      public boolean isEmpty() {
         return this.size.get() == 0;
      }

      public int size() {
         return this.size.get();
      }
   }
}
