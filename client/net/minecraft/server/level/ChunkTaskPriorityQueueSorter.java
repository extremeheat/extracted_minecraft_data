package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class ChunkTaskPriorityQueueSorter implements ChunkHolder.LevelChangeListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<ProcessorHandle<?>, ChunkTaskPriorityQueue<? extends Function<ProcessorHandle<Unit>, ?>>> queues;
   private final Set<ProcessorHandle<?>> sleeping;
   private final ProcessorMailbox<StrictQueue.IntRunnable> mailbox;

   public ChunkTaskPriorityQueueSorter(List<ProcessorHandle<?>> var1, Executor var2, int var3) {
      super();
      this.queues = var1.stream().collect(Collectors.toMap(Function.identity(), var1x -> new ChunkTaskPriorityQueue(var1x.name() + "_queue", var3)));
      this.sleeping = Sets.newHashSet(var1);
      this.mailbox = new ProcessorMailbox<>(new StrictQueue.FixedPriorityQueue(4), var2, "sorter");
   }

   public boolean hasWork() {
      return this.mailbox.hasWork() || this.queues.values().stream().anyMatch(ChunkTaskPriorityQueue::hasWork);
   }

   public static <T> ChunkTaskPriorityQueueSorter.Message<T> message(Function<ProcessorHandle<Unit>, T> var0, long var1, IntSupplier var3) {
      return new ChunkTaskPriorityQueueSorter.Message<>(var0, var1, var3);
   }

   public static ChunkTaskPriorityQueueSorter.Message<Runnable> message(Runnable var0, long var1, IntSupplier var3) {
      return new ChunkTaskPriorityQueueSorter.Message<>(var1x -> () -> {
            var0.run();
            var1x.tell(Unit.INSTANCE);
         }, var1, var3);
   }

   public static ChunkTaskPriorityQueueSorter.Message<Runnable> message(ChunkHolder var0, Runnable var1) {
      return message(var1, var0.getPos().toLong(), var0::getQueueLevel);
   }

   public static <T> ChunkTaskPriorityQueueSorter.Message<T> message(ChunkHolder var0, Function<ProcessorHandle<Unit>, T> var1) {
      return message(var1, var0.getPos().toLong(), var0::getQueueLevel);
   }

   public static ChunkTaskPriorityQueueSorter.Release release(Runnable var0, long var1, boolean var3) {
      return new ChunkTaskPriorityQueueSorter.Release(var0, var1, var3);
   }

   public <T> ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<T>> getProcessor(ProcessorHandle<T> var1, boolean var2) {
      return this.mailbox
         .<ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<T>>>ask(
            var3 -> new StrictQueue.IntRunnable(
                  0,
                  () -> {
                     this.getQueue(var1);
                     var3.tell(
                        ProcessorHandle.of(
                           "chunk priority sorter around " + var1.name(), var3xx -> this.submit(var1, var3xx.task, var3xx.pos, var3xx.level, var2)
                        )
                     );
                  }
               )
         )
         .join();
   }

   public ProcessorHandle<ChunkTaskPriorityQueueSorter.Release> getReleaseProcessor(ProcessorHandle<Runnable> var1) {
      return this.mailbox
         .<ProcessorHandle<ChunkTaskPriorityQueueSorter.Release>>ask(
            var2 -> new StrictQueue.IntRunnable(
                  0,
                  () -> var2.tell(
                        ProcessorHandle.of(
                           "chunk priority sorter around " + var1.name(), var2xx -> this.release(var1, var2xx.pos, var2xx.task, var2xx.clearQueue)
                        )
                     )
               )
         )
         .join();
   }

   @Override
   public void onLevelChange(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4) {
      this.mailbox.tell(new StrictQueue.IntRunnable(0, () -> {
         int var5 = var2.getAsInt();
         this.queues.values().forEach(var3xx -> var3xx.resortChunkTasks(var5, var1, var3));
         var4.accept(var3);
      }));
   }

   private <T> void release(ProcessorHandle<T> var1, long var2, Runnable var4, boolean var5) {
      this.mailbox.tell(new StrictQueue.IntRunnable(1, () -> {
         ChunkTaskPriorityQueue var6 = this.getQueue(var1);
         var6.release(var2, var5);
         if (this.sleeping.remove(var1)) {
            this.pollTask(var6, var1);
         }

         var4.run();
      }));
   }

   private <T> void submit(ProcessorHandle<T> var1, Function<ProcessorHandle<Unit>, T> var2, long var3, IntSupplier var5, boolean var6) {
      this.mailbox.tell(new StrictQueue.IntRunnable(2, () -> {
         ChunkTaskPriorityQueue var7 = this.getQueue(var1);
         int var8 = var5.getAsInt();
         var7.submit(Optional.of(var2), var3, var8);
         if (var6) {
            var7.submit(Optional.empty(), var3, var8);
         }

         if (this.sleeping.remove(var1)) {
            this.pollTask(var7, var1);
         }
      }));
   }

   private <T> void pollTask(ChunkTaskPriorityQueue<Function<ProcessorHandle<Unit>, T>> var1, ProcessorHandle<T> var2) {
      this.mailbox.tell(new StrictQueue.IntRunnable(3, () -> {
         Stream var3 = var1.pop();
         if (var3 == null) {
            this.sleeping.add(var2);
         } else {
            CompletableFuture.allOf(var3.map(var1xx -> (CompletableFuture)var1xx.map(var2::ask, var0x -> {
                  var0x.run();
                  return CompletableFuture.completedFuture(Unit.INSTANCE);
               })).toArray(var0 -> new CompletableFuture[var0])).thenAccept(var3x -> this.pollTask(var1, var2));
         }
      }));
   }

   private <T> ChunkTaskPriorityQueue<Function<ProcessorHandle<Unit>, T>> getQueue(ProcessorHandle<T> var1) {
      ChunkTaskPriorityQueue var2 = this.queues.get(var1);
      if (var2 == null) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("No queue for: " + var1));
      } else {
         return var2;
      }
   }

   @VisibleForTesting
   public String getDebugStatus() {
      return (String)this.queues
            .entrySet()
            .stream()
            .map(
               var0 -> var0.getKey().name()
                     + "=["
                     + (String)var0.getValue().getAcquired().stream().map(var0x -> var0x + ":" + new ChunkPos(var0x)).collect(Collectors.joining(","))
                     + "]"
            )
            .collect(Collectors.joining(","))
         + ", s="
         + this.sleeping.size();
   }

   @Override
   public void close() {
      this.queues.keySet().forEach(ProcessorHandle::close);
   }

   public static final class Message<T> {
      final Function<ProcessorHandle<Unit>, T> task;
      final long pos;
      final IntSupplier level;

      Message(Function<ProcessorHandle<Unit>, T> var1, long var2, IntSupplier var4) {
         super();
         this.task = var1;
         this.pos = var2;
         this.level = var4;
      }
   }

   public static final class Release {
      final Runnable task;
      final long pos;
      final boolean clearQueue;

      Release(Runnable var1, long var2, boolean var4) {
         super();
         this.task = var1;
         this.pos = var2;
         this.clearQueue = var4;
      }
   }
}
