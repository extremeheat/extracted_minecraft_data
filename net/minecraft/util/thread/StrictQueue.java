package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public interface StrictQueue {
   @Nullable
   Object pop();

   boolean push(Object var1);

   boolean isEmpty();

   public static final class FixedPriorityQueue implements StrictQueue {
      private final List queueList;

      public FixedPriorityQueue(int var1) {
         this.queueList = (List)IntStream.range(0, var1).mapToObj((var0) -> {
            return Queues.newConcurrentLinkedQueue();
         }).collect(Collectors.toList());
      }

      @Nullable
      public Runnable pop() {
         Iterator var1 = this.queueList.iterator();

         Runnable var3;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            Queue var2 = (Queue)var1.next();
            var3 = (Runnable)var2.poll();
         } while(var3 == null);

         return var3;
      }

      public boolean push(StrictQueue.IntRunnable var1) {
         int var2 = var1.getPriority();
         ((Queue)this.queueList.get(var2)).add(var1);
         return true;
      }

      public boolean isEmpty() {
         return this.queueList.stream().allMatch(Collection::isEmpty);
      }

      // $FF: synthetic method
      @Nullable
      public Object pop() {
         return this.pop();
      }
   }

   public static final class IntRunnable implements Runnable {
      private final int priority;
      private final Runnable task;

      public IntRunnable(int var1, Runnable var2) {
         this.priority = var1;
         this.task = var2;
      }

      public void run() {
         this.task.run();
      }

      public int getPriority() {
         return this.priority;
      }
   }

   public static final class QueueStrictQueue implements StrictQueue {
      private final Queue queue;

      public QueueStrictQueue(Queue var1) {
         this.queue = var1;
      }

      @Nullable
      public Object pop() {
         return this.queue.poll();
      }

      public boolean push(Object var1) {
         return this.queue.add(var1);
      }

      public boolean isEmpty() {
         return this.queue.isEmpty();
      }
   }
}
