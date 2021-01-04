package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedLevelLightEngine extends LevelLightEngine implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ProcessorMailbox<Runnable> taskMailbox;
   private final ObjectList<Pair<ThreadedLevelLightEngine.TaskType, Runnable>> lightTasks = new ObjectArrayList();
   private final ChunkMap chunkMap;
   private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> sorterMailbox;
   private volatile int taskPerBatch = 5;
   private final AtomicBoolean scheduled = new AtomicBoolean();

   public ThreadedLevelLightEngine(LightChunkGetter var1, ChunkMap var2, boolean var3, ProcessorMailbox<Runnable> var4, ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> var5) {
      super(var1, true, var3);
      this.chunkMap = var2;
      this.sorterMailbox = var5;
      this.taskMailbox = var4;
   }

   public void close() {
   }

   public int runUpdates(int var1, boolean var2, boolean var3) {
      throw new UnsupportedOperationException("Ran authomatically on a different thread!");
   }

   public void onBlockEmissionIncrease(BlockPos var1, int var2) {
      throw new UnsupportedOperationException("Ran authomatically on a different thread!");
   }

   public void checkBlock(BlockPos var1) {
      BlockPos var2 = var1.immutable();
      this.addTask(var1.getX() >> 4, var1.getZ() >> 4, ThreadedLevelLightEngine.TaskType.POST_UPDATE, Util.name(() -> {
         super.checkBlock(var2);
      }, () -> {
         return "checkBlock " + var2;
      }));
   }

   protected void updateChunkStatus(ChunkPos var1) {
      this.addTask(var1.x, var1.z, () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.retainData(var1, false);
         super.enableLightSources(var1, false);

         int var2;
         for(var2 = -1; var2 < 17; ++var2) {
            super.queueSectionData(LightLayer.BLOCK, SectionPos.of(var1, var2), (DataLayer)null);
            super.queueSectionData(LightLayer.SKY, SectionPos.of(var1, var2), (DataLayer)null);
         }

         for(var2 = 0; var2 < 16; ++var2) {
            super.updateSectionStatus(SectionPos.of(var1, var2), true);
         }

      }, () -> {
         return "updateChunkStatus " + var1 + " " + true;
      }));
   }

   public void updateSectionStatus(SectionPos var1, boolean var2) {
      this.addTask(var1.x(), var1.z(), () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.updateSectionStatus(var1, var2);
      }, () -> {
         return "updateSectionStatus " + var1 + " " + var2;
      }));
   }

   public void enableLightSources(ChunkPos var1, boolean var2) {
      this.addTask(var1.x, var1.z, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.enableLightSources(var1, var2);
      }, () -> {
         return "enableLight " + var1 + " " + var2;
      }));
   }

   public void queueSectionData(LightLayer var1, SectionPos var2, @Nullable DataLayer var3) {
      this.addTask(var2.x(), var2.z(), () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.queueSectionData(var1, var2, var3);
      }, () -> {
         return "queueData " + var2;
      }));
   }

   private void addTask(int var1, int var2, ThreadedLevelLightEngine.TaskType var3, Runnable var4) {
      this.addTask(var1, var2, this.chunkMap.getChunkQueueLevel(ChunkPos.asLong(var1, var2)), var3, var4);
   }

   private void addTask(int var1, int var2, IntSupplier var3, ThreadedLevelLightEngine.TaskType var4, Runnable var5) {
      this.sorterMailbox.tell(ChunkTaskPriorityQueueSorter.message(() -> {
         this.lightTasks.add(Pair.of(var4, var5));
         if (this.lightTasks.size() >= this.taskPerBatch) {
            this.runUpdate();
         }

      }, ChunkPos.asLong(var1, var2), var3));
   }

   public void retainData(ChunkPos var1, boolean var2) {
      this.addTask(var1.x, var1.z, () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.retainData(var1, var2);
      }, () -> {
         return "retainData " + var1;
      }));
   }

   public CompletableFuture<ChunkAccess> lightChunk(ChunkAccess var1, boolean var2) {
      ChunkPos var3 = var1.getPos();
      var1.setLightCorrect(false);
      this.addTask(var3.x, var3.z, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         LevelChunkSection[] var4 = var1.getSections();

         for(int var5 = 0; var5 < 16; ++var5) {
            LevelChunkSection var6 = var4[var5];
            if (!LevelChunkSection.isEmpty(var6)) {
               super.updateSectionStatus(SectionPos.of(var3, var5), false);
            }
         }

         super.enableLightSources(var3, true);
         if (!var2) {
            var1.getLights().forEach((var2x) -> {
               super.onBlockEmissionIncrease(var2x, var1.getLightEmission(var2x));
            });
         }

         this.chunkMap.releaseLightTicket(var3);
      }, () -> {
         return "lightChunk " + var3 + " " + var2;
      }));
      return CompletableFuture.supplyAsync(() -> {
         var1.setLightCorrect(true);
         super.retainData(var3, false);
         return var1;
      }, (var2x) -> {
         this.addTask(var3.x, var3.z, ThreadedLevelLightEngine.TaskType.POST_UPDATE, var2x);
      });
   }

   public void tryScheduleUpdate() {
      if ((!this.lightTasks.isEmpty() || super.hasLightWork()) && this.scheduled.compareAndSet(false, true)) {
         this.taskMailbox.tell(() -> {
            this.runUpdate();
            this.scheduled.set(false);
         });
      }

   }

   private void runUpdate() {
      int var1 = Math.min(this.lightTasks.size(), this.taskPerBatch);
      ObjectListIterator var2 = this.lightTasks.iterator();

      int var3;
      Pair var4;
      for(var3 = 0; var2.hasNext() && var3 < var1; ++var3) {
         var4 = (Pair)var2.next();
         if (var4.getFirst() == ThreadedLevelLightEngine.TaskType.PRE_UPDATE) {
            ((Runnable)var4.getSecond()).run();
         }
      }

      var2.back(var3);
      super.runUpdates(2147483647, true, true);

      for(var3 = 0; var2.hasNext() && var3 < var1; ++var3) {
         var4 = (Pair)var2.next();
         if (var4.getFirst() == ThreadedLevelLightEngine.TaskType.POST_UPDATE) {
            ((Runnable)var4.getSecond()).run();
         }

         var2.remove();
      }

   }

   public void setTaskPerBatch(int var1) {
      this.taskPerBatch = var1;
   }

   static enum TaskType {
      PRE_UPDATE,
      POST_UPDATE;

      private TaskType() {
      }
   }
}
