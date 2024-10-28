package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StaticCache2D;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;

public abstract class GenerationChunkHolder {
   private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.getStatusList();
   private static final ChunkResult<ChunkAccess> NOT_DONE_YET = ChunkResult.error("Not done yet");
   public static final ChunkResult<ChunkAccess> UNLOADED_CHUNK = ChunkResult.error("Unloaded chunk");
   public static final CompletableFuture<ChunkResult<ChunkAccess>> UNLOADED_CHUNK_FUTURE;
   protected final ChunkPos pos;
   @Nullable
   private volatile ChunkStatus highestAllowedStatus;
   private final AtomicReference<ChunkStatus> startedWork = new AtomicReference();
   private final AtomicReferenceArray<CompletableFuture<ChunkResult<ChunkAccess>>> futures;
   private final AtomicReference<ChunkGenerationTask> task;
   private final AtomicInteger generationRefCount;

   public GenerationChunkHolder(ChunkPos var1) {
      super();
      this.futures = new AtomicReferenceArray(CHUNK_STATUSES.size());
      this.task = new AtomicReference();
      this.generationRefCount = new AtomicInteger();
      this.pos = var1;
   }

   public CompletableFuture<ChunkResult<ChunkAccess>> scheduleChunkGenerationTask(ChunkStatus var1, ChunkMap var2) {
      if (this.isStatusDisallowed(var1)) {
         return UNLOADED_CHUNK_FUTURE;
      } else {
         CompletableFuture var3 = this.getOrCreateFuture(var1);
         if (var3.isDone()) {
            return var3;
         } else {
            ChunkGenerationTask var4 = (ChunkGenerationTask)this.task.get();
            if (var4 == null || var1.isAfter(var4.targetStatus)) {
               this.rescheduleChunkTask(var2, var1);
            }

            return var3;
         }
      }
   }

   CompletableFuture<ChunkResult<ChunkAccess>> applyStep(ChunkStep var1, GeneratingChunkMap var2, StaticCache2D<GenerationChunkHolder> var3) {
      if (this.isStatusDisallowed(var1.targetStatus())) {
         return UNLOADED_CHUNK_FUTURE;
      } else {
         return this.acquireStatusBump(var1.targetStatus()) ? var2.applyStep(this, var1, var3).handle((var2x, var3x) -> {
            if (var3x != null) {
               CrashReport var4 = CrashReport.forThrowable(var3x, "Exception chunk generation/loading");
               MinecraftServer.setFatalException(new ReportedException(var4));
            } else {
               this.completeFuture(var1.targetStatus(), var2x);
            }

            return ChunkResult.of(var2x);
         }) : this.getOrCreateFuture(var1.targetStatus());
      }
   }

   protected void updateHighestAllowedStatus(ChunkMap var1) {
      ChunkStatus var2 = this.highestAllowedStatus;
      ChunkStatus var3 = ChunkLevel.generationStatus(this.getTicketLevel());
      this.highestAllowedStatus = var3;
      boolean var4 = var2 != null && (var3 == null || var3.isBefore(var2));
      if (var4) {
         this.failAndClearPendingFuturesBetween(var3, var2);
         if (this.task.get() != null) {
            this.rescheduleChunkTask(var1, this.findHighestStatusWithPendingFuture(var3));
         }
      }

   }

   public void replaceProtoChunk(ImposterProtoChunk var1) {
      CompletableFuture var2 = CompletableFuture.completedFuture(ChunkResult.of(var1));

      for(int var3 = 0; var3 < this.futures.length() - 1; ++var3) {
         CompletableFuture var4 = (CompletableFuture)this.futures.get(var3);
         Objects.requireNonNull(var4);
         ChunkAccess var5 = (ChunkAccess)((ChunkResult)var4.getNow(NOT_DONE_YET)).orElse((Object)null);
         if (!(var5 instanceof ProtoChunk)) {
            throw new IllegalStateException("Trying to replace a ProtoChunk, but found " + String.valueOf(var5));
         }

         if (!this.futures.compareAndSet(var3, var4, var2)) {
            throw new IllegalStateException("Future changed by other thread while trying to replace it");
         }
      }

   }

   void removeTask(ChunkGenerationTask var1) {
      this.task.compareAndSet(var1, (Object)null);
   }

   private void rescheduleChunkTask(ChunkMap var1, @Nullable ChunkStatus var2) {
      ChunkGenerationTask var3;
      if (var2 != null) {
         var3 = var1.scheduleGenerationTask(var2, this.getPos());
      } else {
         var3 = null;
      }

      ChunkGenerationTask var4 = (ChunkGenerationTask)this.task.getAndSet(var3);
      if (var4 != null) {
         var4.markForCancellation();
      }

   }

   private CompletableFuture<ChunkResult<ChunkAccess>> getOrCreateFuture(ChunkStatus var1) {
      if (this.isStatusDisallowed(var1)) {
         return UNLOADED_CHUNK_FUTURE;
      } else {
         int var2 = var1.getIndex();
         CompletableFuture var3 = (CompletableFuture)this.futures.get(var2);

         CompletableFuture var4;
         do {
            if (var3 != null) {
               return var3;
            }

            var4 = new CompletableFuture();
            var3 = (CompletableFuture)this.futures.compareAndExchange(var2, (Object)null, var4);
         } while(var3 != null);

         if (this.isStatusDisallowed(var1)) {
            this.failAndClearPendingFuture(var2, var4);
            return UNLOADED_CHUNK_FUTURE;
         } else {
            return var4;
         }
      }
   }

   private void failAndClearPendingFuturesBetween(@Nullable ChunkStatus var1, ChunkStatus var2) {
      int var3 = var1 == null ? 0 : var1.getIndex() + 1;
      int var4 = var2.getIndex();

      for(int var5 = var3; var5 <= var4; ++var5) {
         CompletableFuture var6 = (CompletableFuture)this.futures.get(var5);
         if (var6 != null) {
            this.failAndClearPendingFuture(var5, var6);
         }
      }

   }

   private void failAndClearPendingFuture(int var1, CompletableFuture<ChunkResult<ChunkAccess>> var2) {
      if (var2.complete(UNLOADED_CHUNK) && !this.futures.compareAndSet(var1, var2, (Object)null)) {
         throw new IllegalStateException("Nothing else should replace the future here");
      }
   }

   private void completeFuture(ChunkStatus var1, ChunkAccess var2) {
      ChunkResult var3 = ChunkResult.of(var2);
      int var4 = var1.getIndex();

      do {
         while(true) {
            CompletableFuture var5 = (CompletableFuture)this.futures.get(var4);
            if (var5 == null) {
               break;
            }

            if (var5.complete(var3)) {
               return;
            }

            if (((ChunkResult)var5.getNow(NOT_DONE_YET)).isSuccess()) {
               throw new IllegalStateException("Trying to complete a future but found it to be completed successfully already");
            }

            Thread.yield();
         }
      } while(!this.futures.compareAndSet(var4, (Object)null, CompletableFuture.completedFuture(var3)));

   }

   @Nullable
   private ChunkStatus findHighestStatusWithPendingFuture(@Nullable ChunkStatus var1) {
      if (var1 == null) {
         return null;
      } else {
         ChunkStatus var2 = var1;

         for(ChunkStatus var3 = (ChunkStatus)this.startedWork.get(); var3 == null || var2.isAfter(var3); var2 = var2.getParent()) {
            if (this.futures.get(var2.getIndex()) != null) {
               return var2;
            }

            if (var2 == ChunkStatus.EMPTY) {
               break;
            }
         }

         return null;
      }
   }

   private boolean acquireStatusBump(ChunkStatus var1) {
      ChunkStatus var2 = var1 == ChunkStatus.EMPTY ? null : var1.getParent();
      ChunkStatus var3 = (ChunkStatus)this.startedWork.compareAndExchange(var2, var1);
      if (var3 == var2) {
         return true;
      } else if (var3 != null && !var1.isAfter(var3)) {
         return false;
      } else {
         String var10002 = String.valueOf(var3);
         throw new IllegalStateException("Unexpected last startedWork status: " + var10002 + " while trying to start: " + String.valueOf(var1));
      }
   }

   private boolean isStatusDisallowed(ChunkStatus var1) {
      ChunkStatus var2 = this.highestAllowedStatus;
      return var2 == null || var1.isAfter(var2);
   }

   public void increaseGenerationRefCount() {
      this.generationRefCount.incrementAndGet();
   }

   public void decreaseGenerationRefCount() {
      int var1 = this.generationRefCount.decrementAndGet();
      if (var1 < 0) {
         throw new IllegalStateException("More releases than claims. Count: " + var1);
      }
   }

   public int getGenerationRefCount() {
      return this.generationRefCount.get();
   }

   @Nullable
   public ChunkAccess getChunkIfPresentUnchecked(ChunkStatus var1) {
      CompletableFuture var2 = (CompletableFuture)this.futures.get(var1.getIndex());
      return var2 == null ? null : (ChunkAccess)((ChunkResult)var2.getNow(NOT_DONE_YET)).orElse((Object)null);
   }

   @Nullable
   public ChunkAccess getChunkIfPresent(ChunkStatus var1) {
      return this.isStatusDisallowed(var1) ? null : this.getChunkIfPresentUnchecked(var1);
   }

   @Nullable
   public ChunkAccess getLatestChunk() {
      ChunkStatus var1 = (ChunkStatus)this.startedWork.get();
      if (var1 == null) {
         return null;
      } else {
         ChunkAccess var2 = this.getChunkIfPresentUnchecked(var1);
         return var2 != null ? var2 : this.getChunkIfPresentUnchecked(var1.getParent());
      }
   }

   @Nullable
   public ChunkStatus getPersistedStatus() {
      CompletableFuture var1 = (CompletableFuture)this.futures.get(ChunkStatus.EMPTY.getIndex());
      ChunkAccess var2 = var1 == null ? null : (ChunkAccess)((ChunkResult)var1.getNow(NOT_DONE_YET)).orElse((Object)null);
      return var2 == null ? null : var2.getPersistedStatus();
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public FullChunkStatus getFullStatus() {
      return ChunkLevel.fullStatus(this.getTicketLevel());
   }

   public abstract int getTicketLevel();

   public abstract int getQueueLevel();

   @VisibleForDebug
   public List<Pair<ChunkStatus, CompletableFuture<ChunkResult<ChunkAccess>>>> getAllFutures() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < CHUNK_STATUSES.size(); ++var2) {
         var1.add(Pair.of((ChunkStatus)CHUNK_STATUSES.get(var2), (CompletableFuture)this.futures.get(var2)));
      }

      return var1;
   }

   @Nullable
   @VisibleForDebug
   public ChunkStatus getLatestStatus() {
      for(int var1 = CHUNK_STATUSES.size() - 1; var1 >= 0; --var1) {
         ChunkStatus var2 = (ChunkStatus)CHUNK_STATUSES.get(var1);
         ChunkAccess var3 = this.getChunkIfPresentUnchecked(var2);
         if (var3 != null) {
            return var2;
         }
      }

      return null;
   }

   static {
      UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
   }
}
