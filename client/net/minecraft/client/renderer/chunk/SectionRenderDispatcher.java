package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.TracingExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SectionRenderDispatcher {
   private final CompileTaskDynamicQueue compileQueue = new CompileTaskDynamicQueue();
   private final Queue<Runnable> toUpload = Queues.newConcurrentLinkedQueue();
   final Executor mainThreadUploadExecutor;
   final Queue<SectionMesh> toClose;
   final SectionBufferBuilderPack fixedBuffers;
   private final SectionBufferBuilderPool bufferPool;
   volatile boolean closed;
   private final ConsecutiveExecutor consecutiveExecutor;
   private final TracingExecutor executor;
   ClientLevel level;
   final LevelRenderer renderer;
   Vec3 cameraPosition;
   final SectionCompiler sectionCompiler;

   public SectionRenderDispatcher(ClientLevel var1, LevelRenderer var2, TracingExecutor var3, RenderBuffers var4, BlockRenderDispatcher var5, BlockEntityRenderDispatcher var6) {
      super();
      Queue var10001 = this.toUpload;
      Objects.requireNonNull(var10001);
      this.mainThreadUploadExecutor = var10001::add;
      this.toClose = Queues.newConcurrentLinkedQueue();
      this.cameraPosition = Vec3.ZERO;
      this.level = var1;
      this.renderer = var2;
      this.fixedBuffers = var4.fixedBufferPack();
      this.bufferPool = var4.sectionBufferPool();
      this.executor = var3;
      this.consecutiveExecutor = new ConsecutiveExecutor(var3, "Section Renderer");
      this.consecutiveExecutor.schedule(this::runTask);
      this.sectionCompiler = new SectionCompiler(var5, var6);
   }

   public void setLevel(ClientLevel var1) {
      this.level = var1;
   }

   private void runTask() {
      if (!this.closed && !this.bufferPool.isEmpty()) {
         RenderSection.CompileTask var1 = this.compileQueue.poll(this.cameraPosition);
         if (var1 != null) {
            SectionBufferBuilderPack var2 = (SectionBufferBuilderPack)Objects.requireNonNull(this.bufferPool.acquire());
            CompletableFuture.supplyAsync(() -> var1.doTask(var2), this.executor.forName(var1.name())).thenCompose((var0) -> var0).whenComplete((var3, var4) -> {
               if (var4 != null) {
                  Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var4, "Batching sections"));
               } else {
                  var1.isCompleted.set(true);
                  this.consecutiveExecutor.schedule(() -> {
                     if (var3 == SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL) {
                        var2.clearAll();
                     } else {
                        var2.discardAll();
                     }

                     this.bufferPool.release(var2);
                     this.runTask();
                  });
               }
            });
         }
      }
   }

   public void setCameraPosition(Vec3 var1) {
      this.cameraPosition = var1;
   }

   public void uploadAllPendingUploads() {
      Runnable var1;
      while((var1 = (Runnable)this.toUpload.poll()) != null) {
         var1.run();
      }

      SectionMesh var2;
      while((var2 = (SectionMesh)this.toClose.poll()) != null) {
         var2.close();
      }

   }

   public void rebuildSectionSync(RenderSection var1, RenderRegionCache var2) {
      var1.compileSync(var2);
   }

   public void schedule(RenderSection.CompileTask var1) {
      if (!this.closed) {
         this.consecutiveExecutor.schedule(() -> {
            if (!this.closed) {
               this.compileQueue.add(var1);
               this.runTask();
            }
         });
      }
   }

   public void clearCompileQueue() {
      this.compileQueue.clear();
   }

   public boolean isQueueEmpty() {
      return this.compileQueue.size() == 0 && this.toUpload.isEmpty();
   }

   public void dispose() {
      this.closed = true;
      this.clearCompileQueue();
      this.uploadAllPendingUploads();
   }

   @VisibleForDebug
   public String getStats() {
      return String.format(Locale.ROOT, "pC: %03d, pU: %02d, aB: %02d", this.compileQueue.size(), this.toUpload.size(), this.bufferPool.getFreeBufferCount());
   }

   @VisibleForDebug
   public int getCompileQueueSize() {
      return this.compileQueue.size();
   }

   @VisibleForDebug
   public int getToUpload() {
      return this.toUpload.size();
   }

   @VisibleForDebug
   public int getFreeBufferCount() {
      return this.bufferPool.getFreeBufferCount();
   }

   public class RenderSection {
      public static final int SIZE = 16;
      public final int index;
      public final AtomicReference<SectionMesh> sectionMesh;
      @Nullable
      private RebuildTask lastRebuildTask;
      @Nullable
      private ResortTransparencyTask lastResortTransparencyTask;
      private AABB bb;
      private boolean dirty;
      volatile long sectionNode;
      final BlockPos.MutableBlockPos renderOrigin;
      private boolean playerChanged;

      public RenderSection(final int var2, final long var3) {
         super();
         this.sectionMesh = new AtomicReference(CompiledSectionMesh.UNCOMPILED);
         this.dirty = true;
         this.sectionNode = SectionPos.asLong(-1, -1, -1);
         this.renderOrigin = new BlockPos.MutableBlockPos(-1, -1, -1);
         this.index = var2;
         this.setSectionNode(var3);
      }

      private boolean doesChunkExistAt(long var1) {
         ChunkAccess var3 = SectionRenderDispatcher.this.level.getChunk(SectionPos.x(var1), SectionPos.z(var1), ChunkStatus.FULL, false);
         return var3 != null && SectionRenderDispatcher.this.level.getLightEngine().lightOnInColumn(SectionPos.getZeroNode(var1));
      }

      public boolean hasAllNeighbors() {
         return this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.WEST)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.NORTH)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.EAST)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.SOUTH)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, -1, 0, -1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, -1, 0, 1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, 1, 0, -1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, 1, 0, 1));
      }

      public AABB getBoundingBox() {
         return this.bb;
      }

      public CompletableFuture<Void> upload(Map<ChunkSectionLayer, MeshData> var1, CompiledSectionMesh var2) {
         if (SectionRenderDispatcher.this.closed) {
            var1.values().forEach(MeshData::close);
            return CompletableFuture.completedFuture((Object)null);
         } else {
            return CompletableFuture.runAsync(() -> var1.forEach((var2x, var3) -> {
                  try (Zone var4 = Profiler.get().zone("Upload Section Layer")) {
                     var2.uploadMeshLayer(var2x, var3, this.sectionNode);
                     var3.close();
                  }

               }), SectionRenderDispatcher.this.mainThreadUploadExecutor);
         }
      }

      public CompletableFuture<Void> uploadSectionIndexBuffer(CompiledSectionMesh var1, ByteBufferBuilder.Result var2, ChunkSectionLayer var3) {
         if (SectionRenderDispatcher.this.closed) {
            var2.close();
            return CompletableFuture.completedFuture((Object)null);
         } else {
            return CompletableFuture.runAsync(() -> {
               try (Zone var4 = Profiler.get().zone("Upload Section Indices")) {
                  var1.uploadLayerIndexBuffer(var3, var2, this.sectionNode);
                  var2.close();
               }

            }, SectionRenderDispatcher.this.mainThreadUploadExecutor);
         }
      }

      public void setSectionNode(long var1) {
         this.reset();
         this.sectionNode = var1;
         int var3 = SectionPos.sectionToBlockCoord(SectionPos.x(var1));
         int var4 = SectionPos.sectionToBlockCoord(SectionPos.y(var1));
         int var5 = SectionPos.sectionToBlockCoord(SectionPos.z(var1));
         this.renderOrigin.set(var3, var4, var5);
         this.bb = new AABB((double)var3, (double)var4, (double)var5, (double)(var3 + 16), (double)(var4 + 16), (double)(var5 + 16));
      }

      public SectionMesh getSectionMesh() {
         return (SectionMesh)this.sectionMesh.get();
      }

      public void reset() {
         this.cancelTasks();
         ((SectionMesh)this.sectionMesh.getAndSet(CompiledSectionMesh.UNCOMPILED)).close();
         this.dirty = true;
      }

      public BlockPos getRenderOrigin() {
         return this.renderOrigin;
      }

      public long getSectionNode() {
         return this.sectionNode;
      }

      public void setDirty(boolean var1) {
         boolean var2 = this.dirty;
         this.dirty = true;
         this.playerChanged = var1 | (var2 && this.playerChanged);
      }

      public void setNotDirty() {
         this.dirty = false;
         this.playerChanged = false;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public boolean isDirtyFromPlayer() {
         return this.dirty && this.playerChanged;
      }

      public long getNeighborSectionNode(Direction var1) {
         return SectionPos.offset(this.sectionNode, var1);
      }

      public void resortTransparency(SectionRenderDispatcher var1) {
         SectionMesh var3 = this.getSectionMesh();
         if (var3 instanceof CompiledSectionMesh var2) {
            this.lastResortTransparencyTask = new ResortTransparencyTask(var2);
            var1.schedule(this.lastResortTransparencyTask);
         }

      }

      public boolean hasTranslucentGeometry() {
         return this.getSectionMesh().hasTranslucentGeometry();
      }

      public boolean transparencyResortingScheduled() {
         return this.lastResortTransparencyTask != null && !this.lastResortTransparencyTask.isCompleted.get();
      }

      protected void cancelTasks() {
         if (this.lastRebuildTask != null) {
            this.lastRebuildTask.cancel();
            this.lastRebuildTask = null;
         }

         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
            this.lastResortTransparencyTask = null;
         }

      }

      public CompileTask createCompileTask(RenderRegionCache var1) {
         this.cancelTasks();
         RenderSectionRegion var2 = var1.createRegion(SectionRenderDispatcher.this.level, this.sectionNode);
         boolean var3 = this.sectionMesh.get() != CompiledSectionMesh.UNCOMPILED;
         this.lastRebuildTask = new RebuildTask(var2, var3);
         return this.lastRebuildTask;
      }

      public void rebuildSectionAsync(RenderRegionCache var1) {
         CompileTask var2 = this.createCompileTask(var1);
         SectionRenderDispatcher.this.schedule(var2);
      }

      public void compileSync(RenderRegionCache var1) {
         CompileTask var2 = this.createCompileTask(var1);
         var2.doTask(SectionRenderDispatcher.this.fixedBuffers);
      }

      void setSectionMesh(SectionMesh var1) {
         SectionMesh var2 = (SectionMesh)this.sectionMesh.getAndSet(var1);
         SectionRenderDispatcher.this.toClose.add(var2);
         SectionRenderDispatcher.this.renderer.addRecentlyCompiledSection(this);
      }

      VertexSorting createVertexSorting(SectionPos var1) {
         Vec3 var2 = SectionRenderDispatcher.this.cameraPosition;
         return VertexSorting.byDistance((float)(var2.x - (double)var1.minBlockX()), (float)(var2.y - (double)var1.minBlockY()), (float)(var2.z - (double)var1.minBlockZ()));
      }

      class RebuildTask extends CompileTask {
         protected final RenderSectionRegion region;

         public RebuildTask(final RenderSectionRegion var2, final boolean var3) {
            super(var3);
            this.region = var2;
         }

         protected String name() {
            return "rend_chk_rebuild";
         }

         public CompletableFuture<SectionTaskResult> doTask(SectionBufferBuilderPack var1) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
            } else {
               long var2 = RenderSection.this.sectionNode;
               SectionPos var4 = SectionPos.of(var2);
               if (this.isCancelled.get()) {
                  return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
               } else {
                  SectionCompiler.Results var5;
                  try (Zone var6 = Profiler.get().zone("Compile Section")) {
                     var5 = SectionRenderDispatcher.this.sectionCompiler.compile(var4, this.region, RenderSection.this.createVertexSorting(var4), var1);
                  }

                  TranslucencyPointOfView var11 = TranslucencyPointOfView.of(SectionRenderDispatcher.this.cameraPosition, var2);
                  if (this.isCancelled.get()) {
                     var5.release();
                     return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                  } else {
                     CompiledSectionMesh var7 = new CompiledSectionMesh(var11, var5);
                     CompletableFuture var8 = RenderSection.this.upload(var5.renderedLayers, var7);
                     return var8.handle((var2x, var3) -> {
                        if (var3 != null && !(var3 instanceof CancellationException) && !(var3 instanceof InterruptedException)) {
                           Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var3, "Rendering section"));
                        }

                        if (!this.isCancelled.get() && !SectionRenderDispatcher.this.closed) {
                           RenderSection.this.setSectionMesh(var7);
                           return SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL;
                        } else {
                           SectionRenderDispatcher.this.toClose.add(var7);
                           return SectionRenderDispatcher.SectionTaskResult.CANCELLED;
                        }
                     });
                  }
               }
            }
         }

         public void cancel() {
            if (this.isCancelled.compareAndSet(false, true)) {
               RenderSection.this.setDirty(false);
            }

         }
      }

      class ResortTransparencyTask extends CompileTask {
         private final CompiledSectionMesh compiledSectionMesh;

         public ResortTransparencyTask(final CompiledSectionMesh var2) {
            super(true);
            this.compiledSectionMesh = var2;
         }

         protected String name() {
            return "rend_chk_sort";
         }

         public CompletableFuture<SectionTaskResult> doTask(SectionBufferBuilderPack var1) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
            } else {
               MeshData.SortState var2 = this.compiledSectionMesh.getTransparencyState();
               if (var2 != null && !this.compiledSectionMesh.isEmpty(ChunkSectionLayer.TRANSLUCENT)) {
                  long var3 = RenderSection.this.sectionNode;
                  VertexSorting var5 = RenderSection.this.createVertexSorting(SectionPos.of(var3));
                  TranslucencyPointOfView var6 = TranslucencyPointOfView.of(SectionRenderDispatcher.this.cameraPosition, var3);
                  if (!this.compiledSectionMesh.isDifferentPointOfView(var6) && !var6.isAxisAligned()) {
                     return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                  } else {
                     ByteBufferBuilder.Result var7 = var2.buildSortedIndexBuffer(var1.buffer(ChunkSectionLayer.TRANSLUCENT), var5);
                     if (var7 == null) {
                        return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                     } else if (this.isCancelled.get()) {
                        var7.close();
                        return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                     } else {
                        CompletableFuture var8 = RenderSection.this.uploadSectionIndexBuffer(this.compiledSectionMesh, var7, ChunkSectionLayer.TRANSLUCENT);
                        return var8.handle((var2x, var3x) -> {
                           if (var3x != null && !(var3x instanceof CancellationException) && !(var3x instanceof InterruptedException)) {
                              Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var3x, "Rendering section"));
                           }

                           if (this.isCancelled.get()) {
                              return SectionRenderDispatcher.SectionTaskResult.CANCELLED;
                           } else {
                              this.compiledSectionMesh.setTranslucencyPointOfView(var6);
                              return SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL;
                           }
                        });
                     }
                  }
               } else {
                  return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
               }
            }
         }

         public void cancel() {
            this.isCancelled.set(true);
         }
      }

      public abstract class CompileTask {
         protected final AtomicBoolean isCancelled = new AtomicBoolean(false);
         protected final AtomicBoolean isCompleted = new AtomicBoolean(false);
         protected final boolean isRecompile;

         public CompileTask(final boolean var2) {
            super();
            this.isRecompile = var2;
         }

         public abstract CompletableFuture<SectionTaskResult> doTask(SectionBufferBuilderPack var1);

         public abstract void cancel();

         protected abstract String name();

         public boolean isRecompile() {
            return this.isRecompile;
         }

         public BlockPos getRenderOrigin() {
            return RenderSection.this.renderOrigin;
         }
      }
   }

   static enum SectionTaskResult {
      SUCCESSFUL,
      CANCELLED;

      private SectionTaskResult() {
      }

      // $FF: synthetic method
      private static SectionTaskResult[] $values() {
         return new SectionTaskResult[]{SUCCESSFUL, CANCELLED};
      }
   }
}
