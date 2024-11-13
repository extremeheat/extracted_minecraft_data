package net.minecraft.server.level;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;

public class ChunkTaskPriorityQueue {
   public static final int PRIORITY_LEVEL_COUNT;
   private final List<Long2ObjectLinkedOpenHashMap<List<Runnable>>> queuesPerPriority;
   private volatile int topPriorityQueueIndex;
   private final String name;

   public ChunkTaskPriorityQueue(String var1) {
      super();
      this.queuesPerPriority = IntStream.range(0, PRIORITY_LEVEL_COUNT).mapToObj((var0) -> new Long2ObjectLinkedOpenHashMap()).toList();
      this.topPriorityQueueIndex = PRIORITY_LEVEL_COUNT;
      this.name = var1;
   }

   protected void resortChunkTasks(int var1, ChunkPos var2, int var3) {
      if (var1 < PRIORITY_LEVEL_COUNT) {
         Long2ObjectLinkedOpenHashMap var4 = (Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(var1);
         List var5 = (List)var4.remove(var2.toLong());
         if (var1 == this.topPriorityQueueIndex) {
            while(this.hasWork() && ((Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(this.topPriorityQueueIndex)).isEmpty()) {
               ++this.topPriorityQueueIndex;
            }
         }

         if (var5 != null && !var5.isEmpty()) {
            ((List)((Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(var3)).computeIfAbsent(var2.toLong(), (var0) -> Lists.newArrayList())).addAll(var5);
            this.topPriorityQueueIndex = Math.min(this.topPriorityQueueIndex, var3);
         }

      }
   }

   protected void submit(Runnable var1, long var2, int var4) {
      ((List)((Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(var4)).computeIfAbsent(var2, (var0) -> Lists.newArrayList())).add(var1);
      this.topPriorityQueueIndex = Math.min(this.topPriorityQueueIndex, var4);
   }

   protected void release(long var1, boolean var3) {
      for(Long2ObjectLinkedOpenHashMap var5 : this.queuesPerPriority) {
         List var6 = (List)var5.get(var1);
         if (var6 != null) {
            if (var3) {
               var6.clear();
            }

            if (var6.isEmpty()) {
               var5.remove(var1);
            }
         }
      }

      while(this.hasWork() && ((Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(this.topPriorityQueueIndex)).isEmpty()) {
         ++this.topPriorityQueueIndex;
      }

   }

   @Nullable
   public TasksForChunk pop() {
      if (!this.hasWork()) {
         return null;
      } else {
         int var1 = this.topPriorityQueueIndex;
         Long2ObjectLinkedOpenHashMap var2 = (Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(var1);
         long var3 = var2.firstLongKey();

         List var5;
         for(var5 = (List)var2.removeFirst(); this.hasWork() && ((Long2ObjectLinkedOpenHashMap)this.queuesPerPriority.get(this.topPriorityQueueIndex)).isEmpty(); ++this.topPriorityQueueIndex) {
         }

         return new TasksForChunk(var3, var5);
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
      public TasksForChunk(long var1, List<Runnable> var3) {
         super();
         this.chunkPos = var1;
         this.tasks = var3;
      }
   }
}
