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
import net.minecraft.util.Util;
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
   private final Executor mainThreadUploadExecutor;
   private final Queue<SectionMesh> toClose;
   private final SectionBufferBuilderPack fixedBuffers;
   private final SectionBufferBuilderPool bufferPool;
   private volatile boolean closed;
   private final ConsecutiveExecutor consecutiveExecutor;
   private final TracingExecutor executor;
   private ClientLevel level;
   private final LevelRenderer renderer;
   private Vec3 cameraPosition;
   private final SectionCompiler sectionCompiler;

   public SectionRenderDispatcher(final ClientLevel level, final LevelRenderer renderer, final TracingExecutor executor, final RenderBuffers renderBuffers, final BlockRenderDispatcher blockRenderer, final BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
      super();
      Queue var10001 = this.toUpload;
      Objects.requireNonNull(var10001);
      this.mainThreadUploadExecutor = var10001::add;
      this.toClose = Queues.newConcurrentLinkedQueue();
      this.cameraPosition = Vec3.ZERO;
      this.level = level;
      this.renderer = renderer;
      this.fixedBuffers = renderBuffers.fixedBufferPack();
      this.bufferPool = renderBuffers.sectionBufferPool();
      this.executor = executor;
      this.consecutiveExecutor = new ConsecutiveExecutor(executor, "Section Renderer");
      this.consecutiveExecutor.schedule(this::runTask);
      this.sectionCompiler = new SectionCompiler(blockRenderer, blockEntityRenderDispatcher);
   }

   public void setLevel(final ClientLevel level) {
      this.level = level;
   }

   private void runTask() {
      if (!this.closed && !this.bufferPool.isEmpty()) {
         RenderSection.CompileTask task = this.compileQueue.poll(this.cameraPosition);
         if (task != null) {
            SectionBufferBuilderPack buffer = (SectionBufferBuilderPack)Objects.requireNonNull(this.bufferPool.acquire());
            CompletableFuture.supplyAsync(() -> task.doTask(buffer), this.executor.forName(task.name())).thenCompose((f) -> f).whenComplete((result, throwable) -> {
               if (throwable != null) {
                  Minecraft.getInstance().delayCrash(CrashReport.forThrowable(throwable, "Batching sections"));
               } else {
                  task.isCompleted.set(true);
                  this.consecutiveExecutor.schedule(() -> {
                     if (result == SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL) {
                        buffer.clearAll();
                     } else {
                        buffer.discardAll();
                     }

                     this.bufferPool.release(buffer);
                     this.runTask();
                  });
               }
            });
         }
      }
   }

   public void setCameraPosition(final Vec3 cameraPosition) {
      this.cameraPosition = cameraPosition;
   }

   public void uploadAllPendingUploads() {
      Runnable upload;
      while((upload = (Runnable)this.toUpload.poll()) != null) {
         upload.run();
      }

      SectionMesh mesh;
      while((mesh = (SectionMesh)this.toClose.poll()) != null) {
         mesh.close();
      }

   }

   public void rebuildSectionSync(final RenderSection section, final RenderRegionCache cache) {
      section.compileSync(cache);
   }

   public void schedule(final RenderSection.CompileTask task) {
      if (!this.closed) {
         this.consecutiveExecutor.schedule(() -> {
            if (!this.closed) {
               this.compileQueue.add(task);
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
      private RebuildTask lastRebuildTask;
      private ResortTransparencyTask lastResortTransparencyTask;
      private AABB bb;
      private boolean dirty;
      private volatile long sectionNode;
      private final BlockPos.MutableBlockPos renderOrigin;
      private boolean playerChanged;
      private long uploadedTime;
      private long fadeDuration;
      private boolean wasPreviouslyEmpty;

      public RenderSection(final int index, final long sectionNode) {
         Objects.requireNonNull(SectionRenderDispatcher.this);
         super();
         this.sectionMesh = new AtomicReference(CompiledSectionMesh.UNCOMPILED);
         this.dirty = true;
         this.sectionNode = SectionPos.asLong(-1, -1, -1);
         this.renderOrigin = new BlockPos.MutableBlockPos(-1, -1, -1);
         this.index = index;
         this.setSectionNode(sectionNode);
      }

      public float getVisibility(final long now) {
         long elapsed = now - this.uploadedTime;
         return elapsed >= this.fadeDuration ? 1.0F : (float)elapsed / (float)this.fadeDuration;
      }

      public void setFadeDuration(final long fadeDuration) {
         this.fadeDuration = fadeDuration;
      }

      public void setWasPreviouslyEmpty(final boolean wasPreviouslyEmpty) {
         this.wasPreviouslyEmpty = wasPreviouslyEmpty;
      }

      public boolean wasPreviouslyEmpty() {
         return this.wasPreviouslyEmpty;
      }

      private boolean doesChunkExistAt(final long sectionNode) {
         ChunkAccess chunk = SectionRenderDispatcher.this.level.getChunk(SectionPos.x(sectionNode), SectionPos.z(sectionNode), ChunkStatus.FULL, false);
         return chunk != null && SectionRenderDispatcher.this.level.getLightEngine().lightOnInColumn(SectionPos.getZeroNode(sectionNode));
      }

      public boolean hasAllNeighbors() {
         return this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.WEST)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.NORTH)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.EAST)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.SOUTH)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, -1, 0, -1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, -1, 0, 1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, 1, 0, -1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, 1, 0, 1));
      }

      public AABB getBoundingBox() {
         return this.bb;
      }

      public CompletableFuture<Void> upload(final Map<ChunkSectionLayer, MeshData> renderedLayers, final CompiledSectionMesh compiledSectionMesh) {
         if (SectionRenderDispatcher.this.closed) {
            renderedLayers.values().forEach(MeshData::close);
            return CompletableFuture.completedFuture((Object)null);
         } else {
            return CompletableFuture.runAsync(() -> renderedLayers.forEach((layer, mesh) -> {
                  try (Zone ignored = Profiler.get().zone("Upload Section Layer")) {
                     compiledSectionMesh.uploadMeshLayer(layer, mesh, this.sectionNode);
                     mesh.close();
                  }

                  if (this.uploadedTime == 0L) {
                     this.uploadedTime = Util.getMillis();
                  }

               }), SectionRenderDispatcher.this.mainThreadUploadExecutor);
         }
      }

      public CompletableFuture<Void> uploadSectionIndexBuffer(final CompiledSectionMesh compiledSectionMesh, final ByteBufferBuilder.Result indexBuffer, final ChunkSectionLayer layer) {
         if (SectionRenderDispatcher.this.closed) {
            indexBuffer.close();
            return CompletableFuture.completedFuture((Object)null);
         } else {
            return CompletableFuture.runAsync(() -> {
               try (Zone ignored = Profiler.get().zone("Upload Section Indices")) {
                  compiledSectionMesh.uploadLayerIndexBuffer(layer, indexBuffer, this.sectionNode);
                  indexBuffer.close();
               }

            }, SectionRenderDispatcher.this.mainThreadUploadExecutor);
         }
      }

      public void setSectionNode(final long sectionNode) {
         this.reset();
         this.sectionNode = sectionNode;
         int x = SectionPos.sectionToBlockCoord(SectionPos.x(sectionNode));
         int y = SectionPos.sectionToBlockCoord(SectionPos.y(sectionNode));
         int z = SectionPos.sectionToBlockCoord(SectionPos.z(sectionNode));
         this.renderOrigin.set(x, y, z);
         this.bb = new AABB((double)x, (double)y, (double)z, (double)(x + 16), (double)(y + 16), (double)(z + 16));
      }

      public SectionMesh getSectionMesh() {
         return (SectionMesh)this.sectionMesh.get();
      }

      public void reset() {
         this.cancelTasks();
         ((SectionMesh)this.sectionMesh.getAndSet(CompiledSectionMesh.UNCOMPILED)).close();
         this.dirty = true;
         this.uploadedTime = 0L;
         this.wasPreviouslyEmpty = false;
      }

      public BlockPos getRenderOrigin() {
         return this.renderOrigin;
      }

      public long getSectionNode() {
         return this.sectionNode;
      }

      public void setDirty(final boolean fromPlayer) {
         boolean wasDirty = this.dirty;
         this.dirty = true;
         this.playerChanged = fromPlayer | (wasDirty && this.playerChanged);
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

      public long getNeighborSectionNode(final Direction direction) {
         return SectionPos.offset(this.sectionNode, direction);
      }

      public void resortTransparency(final SectionRenderDispatcher dispatcher) {
         SectionMesh var3 = this.getSectionMesh();
         if (var3 instanceof CompiledSectionMesh mesh) {
            this.lastResortTransparencyTask = new ResortTransparencyTask(mesh);
            dispatcher.schedule(this.lastResortTransparencyTask);
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

      public CompileTask createCompileTask(final RenderRegionCache cache) {
         this.cancelTasks();
         RenderSectionRegion region = cache.createRegion(SectionRenderDispatcher.this.level, this.sectionNode);
         boolean isRecompile = this.sectionMesh.get() != CompiledSectionMesh.UNCOMPILED;
         this.lastRebuildTask = new RebuildTask(region, isRecompile);
         return this.lastRebuildTask;
      }

      public void rebuildSectionAsync(final RenderRegionCache cache) {
         CompileTask task = this.createCompileTask(cache);
         SectionRenderDispatcher.this.schedule(task);
      }

      public void compileSync(final RenderRegionCache cache) {
         CompileTask task = this.createCompileTask(cache);
         task.doTask(SectionRenderDispatcher.this.fixedBuffers);
      }

      private void setSectionMesh(final SectionMesh sectionMesh) {
         SectionMesh oldMesh = (SectionMesh)this.sectionMesh.getAndSet(sectionMesh);
         SectionRenderDispatcher.this.toClose.add(oldMesh);
         SectionRenderDispatcher.this.renderer.addRecentlyCompiledSection(this);
      }

      private VertexSorting createVertexSorting(final SectionPos sectionPos) {
         Vec3 camera = SectionRenderDispatcher.this.cameraPosition;
         return VertexSorting.byDistance((float)(camera.x - (double)sectionPos.minBlockX()), (float)(camera.y - (double)sectionPos.minBlockY()), (float)(camera.z - (double)sectionPos.minBlockZ()));
      }

      private class RebuildTask extends CompileTask {
         protected final RenderSectionRegion region;

         public RebuildTask(final RenderSectionRegion region, final boolean isRecompile) {
            Objects.requireNonNull(RenderSection.this);
            super(isRecompile);
            this.region = region;
         }

         protected String name() {
            return "rend_chk_rebuild";
         }

         public CompletableFuture<SectionTaskResult> doTask(final SectionBufferBuilderPack buffers) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
            } else {
               long sectionNode = RenderSection.this.sectionNode;
               SectionPos sectionPos = SectionPos.of(sectionNode);
               if (this.isCancelled.get()) {
                  return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
               } else {
                  SectionCompiler.Results results;
                  try (Zone ignored = Profiler.get().zone("Compile Section")) {
                     results = SectionRenderDispatcher.this.sectionCompiler.compile(sectionPos, this.region, RenderSection.this.createVertexSorting(sectionPos), buffers);
                  }

                  TranslucencyPointOfView translucencyPointOfView = TranslucencyPointOfView.of(SectionRenderDispatcher.this.cameraPosition, sectionNode);
                  if (this.isCancelled.get()) {
                     results.release();
                     return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                  } else {
                     CompiledSectionMesh compiledSectionMesh = new CompiledSectionMesh(translucencyPointOfView, results);
                     CompletableFuture<Void> uploadFuture = RenderSection.this.upload(results.renderedLayers, compiledSectionMesh);
                     return uploadFuture.handle((ignoredx, throwable) -> {
                        if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                           Minecraft.getInstance().delayCrash(CrashReport.forThrowable(throwable, "Rendering section"));
                        }

                        if (!this.isCancelled.get() && !SectionRenderDispatcher.this.closed) {
                           RenderSection.this.setSectionMesh(compiledSectionMesh);
                           return SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL;
                        } else {
                           SectionRenderDispatcher.this.toClose.add(compiledSectionMesh);
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

      private class ResortTransparencyTask extends CompileTask {
         private final CompiledSectionMesh compiledSectionMesh;

         public ResortTransparencyTask(final CompiledSectionMesh compiledSectionMesh) {
            Objects.requireNonNull(RenderSection.this);
            super(true);
            this.compiledSectionMesh = compiledSectionMesh;
         }

         protected String name() {
            return "rend_chk_sort";
         }

         public CompletableFuture<SectionTaskResult> doTask(final SectionBufferBuilderPack buffers) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
            } else {
               MeshData.SortState state = this.compiledSectionMesh.getTransparencyState();
               if (state != null && !this.compiledSectionMesh.isEmpty(ChunkSectionLayer.TRANSLUCENT)) {
                  long sectionNode = RenderSection.this.sectionNode;
                  VertexSorting vertexSorting = RenderSection.this.createVertexSorting(SectionPos.of(sectionNode));
                  TranslucencyPointOfView translucencyPointOfView = TranslucencyPointOfView.of(SectionRenderDispatcher.this.cameraPosition, sectionNode);
                  if (!this.compiledSectionMesh.isDifferentPointOfView(translucencyPointOfView) && !translucencyPointOfView.isAxisAligned()) {
                     return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                  } else {
                     ByteBufferBuilder.Result indexBuffer = state.buildSortedIndexBuffer(buffers.buffer(ChunkSectionLayer.TRANSLUCENT), vertexSorting);
                     if (indexBuffer == null) {
                        return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                     } else if (this.isCancelled.get()) {
                        indexBuffer.close();
                        return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                     } else {
                        CompletableFuture<Void> future = RenderSection.this.uploadSectionIndexBuffer(this.compiledSectionMesh, indexBuffer, ChunkSectionLayer.TRANSLUCENT);
                        return future.handle((ignored, throwable) -> {
                           if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                              Minecraft.getInstance().delayCrash(CrashReport.forThrowable(throwable, "Rendering section"));
                           }

                           if (this.isCancelled.get()) {
                              return SectionRenderDispatcher.SectionTaskResult.CANCELLED;
                           } else {
                              this.compiledSectionMesh.setTranslucencyPointOfView(translucencyPointOfView);
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
         protected final AtomicBoolean isCancelled;
         protected final AtomicBoolean isCompleted;
         protected final boolean isRecompile;

         public CompileTask(final boolean isRecompile) {
            Objects.requireNonNull(RenderSection.this);
            super();
            this.isCancelled = new AtomicBoolean(false);
            this.isCompleted = new AtomicBoolean(false);
            this.isRecompile = isRecompile;
         }

         public abstract CompletableFuture<SectionTaskResult> doTask(final SectionBufferBuilderPack buffers);

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

   private static enum SectionTaskResult {
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
