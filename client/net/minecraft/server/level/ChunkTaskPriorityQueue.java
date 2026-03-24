package net.minecraft.server.level;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

public class ChunkTaskPriorityQueue {
   public static final int PRIORITY_LEVEL_COUNT;
   private final List<Long2ObjectLinkedOpenHashMap<List<Runnable>>> queuesPerPriority;
   private volatile int topPriorityQueueIndex;
   private final String name;

   public ChunkTaskPriorityQueue(final String name) {
      super();
      this.queuesPerPriority = IntStream.range(0, PRIORITY_LEVEL_COUNT).mapToObj((priority) -> new Long2ObjectLinkedOpenHashMap()).toList();
      this.topPriorityQueueIndex = PRIORITY_LEVEL_COUNT;
      this.name = name;
   }

   protected void resortChunkTasks(final int oldPriority, final ChunkPos pos, final int newPriority) {
      if (oldPriority < PRIORITY_LEVEL_COUNT) {
         Long2ObjectLinkedOpenHashMap<List<Runnable>> oldQueue = (Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(oldPriority);
         List<Runnable> oldTasks = (List)oldQueue.remove(pos.pack());
         if (oldPriority == this.topPriorityQueueIndex) {
            while(this.hasWork() && ((Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(this.topPriorityQueueIndex)).isEmpty()) {
               ++this.topPriorityQueueIndex;
            }
         }

         if (oldTasks != null && !oldTasks.isEmpty()) {
            ((List)((Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(newPriority)).computeIfAbsent(pos.pack(), (k) -> Lists.newArrayList())).addAll(oldTasks);
            this.topPriorityQueueIndex = Math.min(this.topPriorityQueueIndex, newPriority);
         }

      }
   }

   protected void submit(final Runnable task, final long chunkPos, final int level) {
      ((List)((Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(level)).computeIfAbsent(chunkPos, (p) -> Lists.newArrayList())).add(task);
      this.topPriorityQueueIndex = Math.min(this.topPriorityQueueIndex, level);
   }

   protected void release(final long pos, final boolean unschedule) {
      for(Long2ObjectLinkedOpenHashMap<List<Runnable>> queue : this.queuesPerPriority) {
         List<Runnable> tasks = (List)queue.get(pos);
         if (tasks != null) {
            if (unschedule) {
               tasks.clear();
            }

            if (tasks.isEmpty()) {
               queue.remove(pos);
            }
         }
      }

      while(this.hasWork() && ((Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(this.topPriorityQueueIndex)).isEmpty()) {
         ++this.topPriorityQueueIndex;
      }

   }

   public @Nullable TasksForChunk pop() {
      if (!this.hasWork()) {
         return null;
      } else {
         int index = this.topPriorityQueueIndex;
         Long2ObjectLinkedOpenHashMap<List<Runnable>> queue = (Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(index);
         long chunkPos = queue.firstLongKey();

         List<Runnable> tasks;
         for(tasks = (List)queue.removeFirst(); this.hasWork() && ((Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(this.topPriorityQueueIndex)).isEmpty(); ++this.topPriorityQueueIndex) {
         }

         return new TasksForChunk(chunkPos, tasks);
      }
   }

   public boolean hasWork() {
      return this.topPriorityQueueIndex < PRIORITY_LEVEL_COUNT;
   }

   public String toString() {
      return this.name + " " + this.topPriorityQueueIndex + "...";
   }

   static {
      PRIORITY_LEVEL_COUNT = ChunkLevel.MAX_LEVEL + 2;
   }

   public static record TasksForChunk(long chunkPos, List<Runnable> tasks) {
      public TasksForChunk {
         super();
      }
   }
}
