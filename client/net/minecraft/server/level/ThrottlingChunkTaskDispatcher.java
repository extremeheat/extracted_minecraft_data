package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.util.thread.TaskScheduler;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

public class ThrottlingChunkTaskDispatcher extends ChunkTaskDispatcher {
   private final LongSet chunkPositionsInExecution = new LongOpenHashSet();
   private final int maxChunksInExecution;
   private final String executorSchedulerName;

   public ThrottlingChunkTaskDispatcher(final TaskScheduler<Runnable> executor, final Executor dispatcherExecutor, final int maxChunksInExecution) {
      super(executor, dispatcherExecutor);
      this.maxChunksInExecution = maxChunksInExecution;
      this.executorSchedulerName = executor.name();
   }

   protected void onRelease(final long key) {
      this.chunkPositionsInExecution.remove(key);
   }

   protected ChunkTaskPriorityQueue.@Nullable TasksForChunk popTasks() {
      return this.chunkPositionsInExecution.size() < this.maxChunksInExecution ? super.popTasks() : null;
   }

   protected void scheduleForExecution(final ChunkTaskPriorityQueue.TasksForChunk tasksForChunk) {
      this.chunkPositionsInExecution.add(tasksForChunk.chunkPos());
      super.scheduleForExecution(tasksForChunk);
   }

   @VisibleForTesting
   public String getDebugStatus() {
      String var10000 = this.executorSchedulerName;
      return var10000 + "=[" + (String)this.chunkPositionsInExecution.longStream().mapToObj((key) -> key + ":" + String.valueOf(ChunkPos.unpack(key))).collect(Collectors.joining(",")) + "], s=" + this.sleeping;
   }
}
