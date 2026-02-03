package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;
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

   public ThreadedLevelLightEngine(final LightChunkGetter lightChunkGetter, final ChunkMap chunkMap, final boolean hasSkyLight, final ConsecutiveExecutor consecutiveExecutor, final ChunkTaskDispatcher taskDispatcher) {
      super(lightChunkGetter, true, hasSkyLight);
      this.chunkMap = chunkMap;
      this.taskDispatcher = taskDispatcher;
      this.consecutiveExecutor = consecutiveExecutor;
   }

   public void close() {
   }

   public int runLightUpdates() {
      throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Ran automatically on a different thread!"));
   }

   public void checkBlock(final BlockPos pos) {
      BlockPos immutable = pos.immutable();
      this.addTask(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name((Runnable)(() -> super.checkBlock(immutable)), () -> "checkBlock " + String.valueOf(immutable)));
   }

   protected void updateChunkStatus(final ChunkPos pos) {
      this.addTask(pos.x(), pos.z(), () -> 0, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name((Runnable)(() -> {
         super.retainData(pos, false);
         super.setLightEnabled(pos, false);

         for(int sectionY = this.getMinLightSection(); sectionY < this.getMaxLightSection(); ++sectionY) {
            super.queueSectionData(LightLayer.BLOCK, SectionPos.of(pos, sectionY), (DataLayer)null);
            super.queueSectionData(LightLayer.SKY, SectionPos.of(pos, sectionY), (DataLayer)null);
         }

         for(int sectionY = this.levelHeightAccessor.getMinSectionY(); sectionY <= this.levelHeightAccessor.getMaxSectionY(); ++sectionY) {
            super.updateSectionStatus(SectionPos.of(pos, sectionY), true);
         }

      }), () -> "updateChunkStatus " + String.valueOf(pos) + " true"));
   }

   public void updateSectionStatus(final SectionPos pos, final boolean sectionEmpty) {
      this.addTask(pos.x(), pos.z(), () -> 0, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name((Runnable)(() -> super.updateSectionStatus(pos, sectionEmpty)), () -> {
         String var10000 = String.valueOf(pos);
         return "updateSectionStatus " + var10000 + " " + sectionEmpty;
      }));
   }

   public void propagateLightSources(final ChunkPos pos) {
      this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name((Runnable)(() -> super.propagateLightSources(pos)), () -> "propagateLight " + String.valueOf(pos)));
   }

   public void setLightEnabled(final ChunkPos pos, final boolean enable) {
      this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name((Runnable)(() -> super.setLightEnabled(pos, enable)), () -> {
         String var10000 = String.valueOf(pos);
         return "enableLight " + var10000 + " " + enable;
      }));
   }

   public void queueSectionData(final LightLayer layer, final SectionPos pos, final @Nullable DataLayer data) {
      this.addTask(pos.x(), pos.z(), () -> 0, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name((Runnable)(() -> super.queueSectionData(layer, pos, data)), () -> "queueData " + String.valueOf(pos)));
   }

   private void addTask(final int chunkX, final int chunkZ, final TaskType type, final Runnable runnable) {
      this.addTask(chunkX, chunkZ, this.chunkMap.getChunkQueueLevel(ChunkPos.pack(chunkX, chunkZ)), type, runnable);
   }

   private void addTask(final int chunkX, final int chunkZ, final IntSupplier level, final TaskType type, final Runnable runnable) {
      this.taskDispatcher.submit(() -> {
         this.lightTasks.add(Pair.of(type, runnable));
         if (this.lightTasks.size() >= 1000) {
            this.runUpdate();
         }

      }, ChunkPos.pack(chunkX, chunkZ), level);
   }

   public void retainData(final ChunkPos pos, final boolean retain) {
      this.addTask(pos.x(), pos.z(), () -> 0, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name((Runnable)(() -> super.retainData(pos, retain)), () -> "retainData " + String.valueOf(pos)));
   }

   public CompletableFuture<ChunkAccess> initializeLight(final ChunkAccess chunk, final boolean lighted) {
      ChunkPos pos = chunk.getPos();
      this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name((Runnable)(() -> {
         LevelChunkSection[] sections = chunk.getSections();

         for(int sectionIndex = 0; sectionIndex < chunk.getSectionsCount(); ++sectionIndex) {
            LevelChunkSection section = sections[sectionIndex];
            if (!section.hasOnlyAir()) {
               int sectionY = this.levelHeightAccessor.getSectionYFromSectionIndex(sectionIndex);
               super.updateSectionStatus(SectionPos.of(pos, sectionY), false);
            }
         }

      }), () -> "initializeLight: " + String.valueOf(pos)));
      return CompletableFuture.supplyAsync(() -> {
         super.setLightEnabled(pos, lighted);
         super.retainData(pos, false);
         return chunk;
      }, (r) -> this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.POST_UPDATE, r));
   }

   public CompletableFuture<ChunkAccess> lightChunk(final ChunkAccess centerChunk, final boolean lighted) {
      ChunkPos pos = centerChunk.getPos();
      centerChunk.setLightCorrect(false);
      this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name((Runnable)(() -> {
         if (!lighted) {
            super.propagateLightSources(pos);
         }

         if (SharedConstants.DEBUG_VERBOSE_SERVER_EVENTS) {
            LOGGER.debug("LIT {}", pos);
         }

      }), () -> {
         String var10000 = String.valueOf(pos);
         return "lightChunk " + var10000 + " " + lighted;
      }));
      return CompletableFuture.supplyAsync(() -> {
         centerChunk.setLightCorrect(true);
         return centerChunk;
      }, (r) -> this.addTask(pos.x(), pos.z(), ThreadedLevelLightEngine.TaskType.POST_UPDATE, r));
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
      int totalSize = Math.min(this.lightTasks.size(), 1000);
      ObjectListIterator<Pair<TaskType, Runnable>> iterator = this.lightTasks.iterator();

      int count;
      for(count = 0; iterator.hasNext() && count < totalSize; ++count) {
         Pair<TaskType, Runnable> task = (Pair)iterator.next();
         if (task.getFirst() == ThreadedLevelLightEngine.TaskType.PRE_UPDATE) {
            ((Runnable)task.getSecond()).run();
         }
      }

      iterator.back(count);
      super.runLightUpdates();

      for(int var5 = 0; iterator.hasNext() && var5 < totalSize; ++var5) {
         Pair<TaskType, Runnable> task = (Pair)iterator.next();
         if (task.getFirst() == ThreadedLevelLightEngine.TaskType.POST_UPDATE) {
            ((Runnable)task.getSecond()).run();
         }

         iterator.remove();
      }

   }

   public CompletableFuture<?> waitForPendingTasks(final int chunkX, final int chunkZ) {
      return CompletableFuture.runAsync(() -> {
      }, (r) -> this.addTask(chunkX, chunkZ, ThreadedLevelLightEngine.TaskType.POST_UPDATE, r));
   }

   private static enum TaskType {
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
