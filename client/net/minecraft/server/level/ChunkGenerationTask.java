package net.minecraft.server.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkDependencies;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class ChunkGenerationTask {
   private final GeneratingChunkMap chunkMap;
   private final ChunkPos pos;
   @Nullable
   private ChunkStatus scheduledStatus = null;
   public final ChunkStatus targetStatus;
   private volatile boolean markedForCancellation;
   private final List<CompletableFuture<ChunkResult<ChunkAccess>>> scheduledLayer = new ArrayList();
   private final StaticCache2D<GenerationChunkHolder> cache;
   private boolean needsGeneration;

   private ChunkGenerationTask(GeneratingChunkMap var1, ChunkStatus var2, ChunkPos var3, StaticCache2D<GenerationChunkHolder> var4) {
      super();
      this.chunkMap = var1;
      this.targetStatus = var2;
      this.pos = var3;
      this.cache = var4;
   }

   public static ChunkGenerationTask create(GeneratingChunkMap var0, ChunkStatus var1, ChunkPos var2) {
      int var3 = ChunkPyramid.GENERATION_PYRAMID.getStepTo(var1).getAccumulatedRadiusOf(ChunkStatus.EMPTY);
      StaticCache2D var4 = StaticCache2D.create(var2.x, var2.z, var3, (var1x, var2x) -> {
         return var0.acquireGeneration(ChunkPos.asLong(var1x, var2x));
      });
      return new ChunkGenerationTask(var0, var1, var2, var4);
   }

   @Nullable
   public CompletableFuture<?> runUntilWait() {
      while(true) {
         CompletableFuture var1 = this.waitForScheduledLayer();
         if (var1 != null) {
            return var1;
         }

         if (this.markedForCancellation || this.scheduledStatus == this.targetStatus) {
            this.releaseClaim();
            return null;
         }

         this.scheduleNextLayer();
      }
   }

   private void scheduleNextLayer() {
      ChunkStatus var1;
      if (this.scheduledStatus == null) {
         var1 = ChunkStatus.EMPTY;
      } else if (!this.needsGeneration && this.scheduledStatus == ChunkStatus.EMPTY && !this.canLoadWithoutGeneration()) {
         this.needsGeneration = true;
         var1 = ChunkStatus.EMPTY;
      } else {
         var1 = (ChunkStatus)ChunkStatus.getStatusList().get(this.scheduledStatus.getIndex() + 1);
      }

      this.scheduleLayer(var1, this.needsGeneration);
      this.scheduledStatus = var1;
   }

   public void markForCancellation() {
      this.markedForCancellation = true;
   }

   private void releaseClaim() {
      GenerationChunkHolder var1 = (GenerationChunkHolder)this.cache.get(this.pos.x, this.pos.z);
      var1.removeTask(this);
      StaticCache2D var10000 = this.cache;
      GeneratingChunkMap var10001 = this.chunkMap;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::releaseGeneration);
   }

   private boolean canLoadWithoutGeneration() {
      if (this.targetStatus == ChunkStatus.EMPTY) {
         return true;
      } else {
         ChunkStatus var1 = ((GenerationChunkHolder)this.cache.get(this.pos.x, this.pos.z)).getPersistedStatus();
         if (var1 != null && !var1.isBefore(this.targetStatus)) {
            ChunkDependencies var2 = ChunkPyramid.LOADING_PYRAMID.getStepTo(this.targetStatus).accumulatedDependencies();
            int var3 = var2.getRadius();

            for(int var4 = this.pos.x - var3; var4 <= this.pos.x + var3; ++var4) {
               for(int var5 = this.pos.z - var3; var5 <= this.pos.z + var3; ++var5) {
                  int var6 = this.pos.getChessboardDistance(var4, var5);
                  ChunkStatus var7 = var2.get(var6);
                  ChunkStatus var8 = ((GenerationChunkHolder)this.cache.get(var4, var5)).getPersistedStatus();
                  if (var8 == null || var8.isBefore(var7)) {
                     return false;
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public GenerationChunkHolder getCenter() {
      return (GenerationChunkHolder)this.cache.get(this.pos.x, this.pos.z);
   }

   private void scheduleLayer(ChunkStatus var1, boolean var2) {
      int var3 = this.getRadiusForLayer(var1, var2);

      for(int var4 = this.pos.x - var3; var4 <= this.pos.x + var3; ++var4) {
         for(int var5 = this.pos.z - var3; var5 <= this.pos.z + var3; ++var5) {
            GenerationChunkHolder var6 = (GenerationChunkHolder)this.cache.get(var4, var5);
            if (this.markedForCancellation || !this.scheduleChunkInLayer(var1, var2, var6)) {
               return;
            }
         }
      }

   }

   private int getRadiusForLayer(ChunkStatus var1, boolean var2) {
      ChunkPyramid var3 = var2 ? ChunkPyramid.GENERATION_PYRAMID : ChunkPyramid.LOADING_PYRAMID;
      return var3.getStepTo(this.targetStatus).getAccumulatedRadiusOf(var1);
   }

   private boolean scheduleChunkInLayer(ChunkStatus var1, boolean var2, GenerationChunkHolder var3) {
      ChunkStatus var4 = var3.getPersistedStatus();
      boolean var5 = var4 != null && var1.isAfter(var4);
      ChunkPyramid var6 = var5 ? ChunkPyramid.GENERATION_PYRAMID : ChunkPyramid.LOADING_PYRAMID;
      if (var5 && !var2) {
         throw new IllegalStateException("Can't load chunk, but didn't expect to need to generate");
      } else {
         CompletableFuture var7 = var3.applyStep(var6.getStepTo(var1), this.chunkMap, this.cache);
         ChunkResult var8 = (ChunkResult)var7.getNow((Object)null);
         if (var8 == null) {
            this.scheduledLayer.add(var7);
            return true;
         } else if (var8.isSuccess()) {
            return true;
         } else {
            this.markForCancellation();
            return false;
         }
      }
   }

   @Nullable
   private CompletableFuture<?> waitForScheduledLayer() {
      while(!this.scheduledLayer.isEmpty()) {
         CompletableFuture var1 = (CompletableFuture)this.scheduledLayer.getLast();
         ChunkResult var2 = (ChunkResult)var1.getNow((Object)null);
         if (var2 == null) {
            return var1;
         }

         this.scheduledLayer.removeLast();
         if (!var2.isSuccess()) {
            this.markForCancellation();
         }
      }

      return null;
   }
}
