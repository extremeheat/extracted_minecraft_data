package net.minecraft.server.level;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.PriorityConsecutiveExecutor;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.util.thread.TaskScheduler;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ChunkTaskDispatcher implements ChunkHolder.LevelChangeListener, AutoCloseable {
   public static final int DISPATCHER_PRIORITY_COUNT = 4;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ChunkTaskPriorityQueue queue;
   private final TaskScheduler<Runnable> executor;
   private final PriorityConsecutiveExecutor dispatcher;
   protected boolean sleeping;

   public ChunkTaskDispatcher(TaskScheduler<Runnable> var1, Executor var2) {
      super();
      this.queue = new ChunkTaskPriorityQueue(var1.name() + "_queue");
      this.executor = var1;
      this.dispatcher = new PriorityConsecutiveExecutor(4, var2, "dispatcher");
      this.sleeping = true;
   }

   public boolean hasWork() {
      return this.dispatcher.hasWork() || this.queue.hasWork();
   }

   public void onLevelChange(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4) {
      this.dispatcher.schedule(new StrictQueue.RunnableWithPriority(0, () -> {
         int var5 = var2.getAsInt();
         this.queue.resortChunkTasks(var5, var1, var3);
         var4.accept(var3);
      }));
   }

   public void release(long var1, Runnable var3, boolean var4) {
      this.dispatcher.schedule(new StrictQueue.RunnableWithPriority(1, () -> {
         this.queue.release(var1, var4);
         this.onRelease(var1);
         if (this.sleeping) {
            this.sleeping = false;
            this.pollTask();
         }

         var3.run();
      }));
   }

   public void submit(Runnable var1, long var2, IntSupplier var4) {
      this.dispatcher.schedule(new StrictQueue.RunnableWithPriority(2, () -> {
         int var5 = var4.getAsInt();
         this.queue.submit(var1, var2, var5);
         if (this.sleeping) {
            this.sleeping = false;
            this.pollTask();
         }

      }));
   }

   protected void pollTask() {
      this.dispatcher.schedule(new StrictQueue.RunnableWithPriority(3, () -> {
         ChunkTaskPriorityQueue.TasksForChunk var1 = this.popTasks();
         if (var1 == null) {
            this.sleeping = true;
         } else {
            this.scheduleForExecution(var1);
         }

      }));
   }

   protected void scheduleForExecution(ChunkTaskPriorityQueue.TasksForChunk var1) {
      CompletableFuture.allOf((CompletableFuture[])var1.tasks().stream().map((var1x) -> {
         return this.executor.scheduleWithResult((var1) -> {
            var1x.run();
            var1.complete(Unit.INSTANCE);
         });
      }).toArray((var0) -> {
         return new CompletableFuture[var0];
      })).thenAccept((var1x) -> {
         this.pollTask();
      });
   }

   protected void onRelease(long var1) {
   }

   @Nullable
   protected ChunkTaskPriorityQueue.@Nullable TasksForChunk popTasks() {
      return this.queue.pop();
   }

   public void close() {
      this.executor.close();
   }
}
