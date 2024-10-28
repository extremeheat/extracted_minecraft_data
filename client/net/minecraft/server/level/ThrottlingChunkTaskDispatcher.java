package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.util.thread.TaskScheduler;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class ThrottlingChunkTaskDispatcher extends ChunkTaskDispatcher {
   private final LongSet chunkPositionsInExecution = new LongOpenHashSet();
   private final int maxChunksInExecution;
   private final String executorSchedulerName;

   public ThrottlingChunkTaskDispatcher(TaskScheduler<Runnable> var1, Executor var2, int var3) {
      super(var1, var2);
      this.maxChunksInExecution = var3;
      this.executorSchedulerName = var1.name();
   }

   protected void onRelease(long var1) {
      this.chunkPositionsInExecution.remove(var1);
   }

   @Nullable
   protected ChunkTaskPriorityQueue.@Nullable TasksForChunk popTasks() {
      return this.chunkPositionsInExecution.size() < this.maxChunksInExecution ? super.popTasks() : null;
   }

   protected void scheduleForExecution(ChunkTaskPriorityQueue.TasksForChunk var1) {
      this.chunkPositionsInExecution.add(var1.chunkPos());
      super.scheduleForExecution(var1);
   }

   @VisibleForTesting
   public String getDebugStatus() {
      String var10000 = this.executorSchedulerName;
      return var10000 + "=[" + (String)this.chunkPositionsInExecution.stream().map((var0) -> {
         return "" + var0 + ":" + String.valueOf(new ChunkPos(var0));
      }).collect(Collectors.joining(",")) + "], s=" + this.sleeping;
   }
}
