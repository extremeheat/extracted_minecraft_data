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

   public static final class FixedPriorityQueue implements StrictQueue<StrictQueue.RunnableWithPriority> {
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
      @Override
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

      public boolean push(StrictQueue.RunnableWithPriority var1) {
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

   public static final class QueueStrictQueue implements StrictQueue<Runnable> {
      private final Queue<Runnable> queue;

      public QueueStrictQueue(Queue<Runnable> var1) {
         super();
         this.queue = var1;
      }

      @Nullable
      @Override
      public Runnable pop() {
         return this.queue.poll();
      }

      @Override
      public boolean push(Runnable var1) {
         return this.queue.add(var1);
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
