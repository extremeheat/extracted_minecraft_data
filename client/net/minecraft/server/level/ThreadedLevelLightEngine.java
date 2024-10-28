package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
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
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.slf4j.Logger;

public class ThreadedLevelLightEngine extends LevelLightEngine implements AutoCloseable {
   public static final int DEFAULT_BATCH_SIZE = 1000;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ConsecutiveExecutor consecutiveExecutor;
   private final ObjectList<Pair<TaskType, Runnable>> lightTasks = new ObjectArrayList();
   private final ChunkMap chunkMap;
   private final ChunkTaskDispatcher taskDispatcher;
   private final int taskPerBatch = 1000;
   private final AtomicBoolean scheduled = new AtomicBoolean();

   public ThreadedLevelLightEngine(LightChunkGetter var1, ChunkMap var2, boolean var3, ConsecutiveExecutor var4, ChunkTaskDispatcher var5) {
      super(var1, true, var3);
      this.chunkMap = var2;
      this.taskDispatcher = var5;
      this.consecutiveExecutor = var4;
   }

   public void close() {
   }

   public int runLightUpdates() {
      throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Ran automatically on a different thread!"));
   }

   public void checkBlock(BlockPos var1) {
      BlockPos var2 = var1.immutable();
      this.addTask(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.checkBlock(var2);
      }, () -> {
         return "checkBlock " + String.valueOf(var2);
      }));
   }

   protected void updateChunkStatus(ChunkPos var1) {
      this.addTask(var1.x, var1.z, () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.retainData(var1, false);
         super.setLightEnabled(var1, false);

         int var2;
         for(var2 = this.getMinLightSection(); var2 < this.getMaxLightSection(); ++var2) {
            super.queueSectionData(LightLayer.BLOCK, SectionPos.of(var1, var2), (DataLayer)null);
            super.queueSectionData(LightLayer.SKY, SectionPos.of(var1, var2), (DataLayer)null);
         }

         for(var2 = this.levelHeightAccessor.getMinSectionY(); var2 <= this.levelHeightAccessor.getMaxSectionY(); ++var2) {
            super.updateSectionStatus(SectionPos.of(var1, var2), true);
         }

      }, () -> {
         return "updateChunkStatus " + String.valueOf(var1) + " true";
      }));
   }

   public void updateSectionStatus(SectionPos var1, boolean var2) {
      this.addTask(var1.x(), var1.z(), () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.updateSectionStatus(var1, var2);
      }, () -> {
         String var10000 = String.valueOf(var1);
         return "updateSectionStatus " + var10000 + " " + var2;
      }));
   }

   public void propagateLightSources(ChunkPos var1) {
      this.addTask(var1.x, var1.z, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.propagateLightSources(var1);
      }, () -> {
         return "propagateLight " + String.valueOf(var1);
      }));
   }

   public void setLightEnabled(ChunkPos var1, boolean var2) {
      this.addTask(var1.x, var1.z, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.setLightEnabled(var1, var2);
      }, () -> {
         String var10000 = String.valueOf(var1);
         return "enableLight " + var10000 + " " + var2;
      }));
   }

   public void queueSectionData(LightLayer var1, SectionPos var2, @Nullable DataLayer var3) {
      this.addTask(var2.x(), var2.z(), () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.queueSectionData(var1, var2, var3);
      }, () -> {
         return "queueData " + String.valueOf(var2);
      }));
   }

   private void addTask(int var1, int var2, TaskType var3, Runnable var4) {
      this.addTask(var1, var2, this.chunkMap.getChunkQueueLevel(ChunkPos.asLong(var1, var2)), var3, var4);
   }

   private void addTask(int var1, int var2, IntSupplier var3, TaskType var4, Runnable var5) {
      this.taskDispatcher.submit(() -> {
         this.lightTasks.add(Pair.of(var4, var5));
         if (this.lightTasks.size() >= 1000) {
            this.runUpdate();
         }

      }, ChunkPos.asLong(var1, var2), var3);
   }

   public void retainData(ChunkPos var1, boolean var2) {
      this.addTask(var1.x, var1.z, () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.retainData(var1, var2);
      }, () -> {
         return "retainData " + String.valueOf(var1);
      }));
   }

   public CompletableFuture<ChunkAccess> initializeLight(ChunkAccess var1, boolean var2) {
      ChunkPos var3 = var1.getPos();
      this.addTask(var3.x, var3.z, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         LevelChunkSection[] var3x = var1.getSections();

         for(int var4 = 0; var4 < var1.getSectionsCount(); ++var4) {
            LevelChunkSection var5 = var3x[var4];
            if (!var5.hasOnlyAir()) {
               int var6 = this.levelHeightAccessor.getSectionYFromSectionIndex(var4);
               super.updateSectionStatus(SectionPos.of(var3, var6), false);
            }
         }

      }, () -> {
         return "initializeLight: " + String.valueOf(var3);
      }));
      return CompletableFuture.supplyAsync(() -> {
         super.setLightEnabled(var3, var2);
         super.retainData(var3, false);
         return var1;
      }, (var2x) -> {
         this.addTask(var3.x, var3.z, ThreadedLevelLightEngine.TaskType.POST_UPDATE, var2x);
      });
   }

   public CompletableFuture<ChunkAccess> lightChunk(ChunkAccess var1, boolean var2) {
      ChunkPos var3 = var1.getPos();
      var1.setLightCorrect(false);
      this.addTask(var3.x, var3.z, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         if (!var2) {
            super.propagateLightSources(var3);
         }

      }, () -> {
         String var10000 = String.valueOf(var3);
         return "lightChunk " + var10000 + " " + var2;
      }));
      return CompletableFuture.supplyAsync(() -> {
         var1.setLightCorrect(true);
         return var1;
      }, (var2x) -> {
         this.addTask(var3.x, var3.z, ThreadedLevelLightEngine.TaskType.POST_UPDATE, var2x);
      });
   }

   public void tryScheduleUpdate() {
      if ((!this.lightTasks.isEmpty() || super.hasLightWork()) && this.scheduled.compareAndSet(false, true)) {
         this.consecutiveExecutor.schedule(() -> {
            this.runUpdate();
            this.scheduled.set(false);
         });
      }

   }

   private void runUpdate() {
      int var1 = Math.min(this.lightTasks.size(), 1000);
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
      super.runLightUpdates();

      for(var3 = 0; var2.hasNext() && var3 < var1; ++var3) {
         var4 = (Pair)var2.next();
         if (var4.getFirst() == ThreadedLevelLightEngine.TaskType.POST_UPDATE) {
            ((Runnable)var4.getSecond()).run();
         }

         var2.remove();
      }

   }

   public CompletableFuture<?> waitForPendingTasks(int var1, int var2) {
      return CompletableFuture.runAsync(() -> {
      }, (var3) -> {
         this.addTask(var1, var2, ThreadedLevelLightEngine.TaskType.POST_UPDATE, var3);
      });
   }

   static enum TaskType {
      PRE_UPDATE,
      POST_UPDATE;

      private TaskType() {
      }

      // $FF: synthetic method
      private static TaskType[] $values() {
         return new TaskType[]{PRE_UPDATE, POST_UPDATE};
      }
   }
}
