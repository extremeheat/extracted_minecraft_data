package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.StagingBuffer;
import com.mojang.blaze3d.vertex.TlsfAllocator;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.CrashReport;
import net.minecraft.TracingExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Util;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SectionRenderDispatcher {
   private final CompileTaskDynamicQueue compileQueue = new CompileTaskDynamicQueue();
   private final SectionBufferBuilderPack fixedBuffers;
   private final SectionBufferBuilderPool bufferPool;
   private volatile boolean closed;
   private final TracingExecutor executor;
   private ClientLevel level;
   private final LevelRenderer renderer;
   private final AtomicReference<Vec3> cameraPosition;
   private SectionCompiler sectionCompiler;
   private final StagingBuffer stagingBuffer;
   private final Map<ChunkSectionLayer, SectionUberBuffers> chunkUberBuffers;
   private final ReentrantLock copyLock;

   public SectionRenderDispatcher(final ClientLevel level, final LevelRenderer renderer, final TracingExecutor executor, final RenderBuffers renderBuffers, final SectionCompiler sectionCompiler) {
      super();
      this.cameraPosition = new AtomicReference(Vec3.ZERO);
      this.copyLock = new ReentrantLock();
      this.level = level;
      this.renderer = renderer;
      this.fixedBuffers = renderBuffers.fixedBufferPack();
      this.bufferPool = renderBuffers.sectionBufferPool();
      this.executor = executor;
      this.sectionCompiler = sectionCompiler;
      int vertexBufferHeapSize = 134217728;
      int indexBufferHeapSize = 33554432;
      int stagingBufferSize = 102760448;
      GpuDevice gpuDevice = RenderSystem.getDevice();
      this.stagingBuffer = StagingBuffer.create("Chunk", gpuDevice, 102760448);
      this.chunkUberBuffers = Util.<ChunkSectionLayer, SectionUberBuffers>makeEnumMap(ChunkSectionLayer.class, (layer) -> {
         VertexFormat vertexFormat = layer.pipeline().getVertexFormat();
         UberGpuBuffer<SectionMesh> vertexUberBuffer = new UberGpuBuffer<SectionMesh>(layer.label(), 32, 134217728, vertexFormat.getVertexSize(), this.stagingBuffer);
         UberGpuBuffer<SectionMesh> indexUberBuffer = new UberGpuBuffer<SectionMesh>(layer.label(), 64, 33554432, 8, this.stagingBuffer);
         return new SectionUberBuffers(vertexUberBuffer, indexUberBuffer);
      });
   }

   public void setLevel(final ClientLevel level, final SectionCompiler sectionCompiler) {
      this.level = level;
      this.sectionCompiler = sectionCompiler;
   }

   private void runTask() {
      if (!this.closed) {
         RenderSection.CompileTask task = this.compileQueue.poll((Vec3)this.cameraPosition.get());
         if (task != null && !task.isCompleted.get() && !task.isCancelled.get()) {
            try {
               SectionBufferBuilderPack buffer = (SectionBufferBuilderPack)Objects.requireNonNull(this.bufferPool.acquire());
               RenderSection.CompileTask.SectionTaskResult result = task.doTask(buffer);
               task.isCompleted.set(true);
               if (result == SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.SUCCESSFUL) {
                  buffer.clearAll();
               } else {
                  buffer.discardAll();
               }

               this.bufferPool.release(buffer);
            } catch (NullPointerException var4) {
               this.compileQueue.add(task);
            } catch (Exception e) {
               Minecraft.getInstance().delayCrash(CrashReport.forThrowable(e, "Batching sections"));
            }

         }
      }
   }

   public void setCameraPosition(final Vec3 cameraPosition) {
      this.cameraPosition.set(cameraPosition);
   }

   public @Nullable RenderSectionBufferSlice getRenderSectionSlice(final SectionMesh sectionMesh, final ChunkSectionLayer layer) {
      SectionUberBuffers uberBuffers = (SectionUberBuffers)this.chunkUberBuffers.get(layer);
      TlsfAllocator.Allocation vertexSlice = uberBuffers.vertexBuffer.getAllocation(sectionMesh);
      if (vertexSlice == null) {
         return null;
      } else {
         long vertexBufferOffset = vertexSlice.getOffsetFromHeap();
         TlsfAllocator.Allocation indexSlice = uberBuffers.indexBuffer.getAllocation(sectionMesh);
         long indexBufferOffset = 0L;
         GpuBuffer indexBuffer = null;
         if (indexSlice != null) {
            indexBufferOffset = indexSlice.getOffsetFromHeap();
            indexBuffer = uberBuffers.indexBuffer.getGpuBuffer(indexSlice);
         }

         return new RenderSectionBufferSlice(uberBuffers.vertexBuffer.getGpuBuffer(vertexSlice), vertexBufferOffset, indexBuffer, indexBufferOffset);
      }
   }

   public void lock() {
      this.copyLock.lock();
   }

   public void unlock() {
      this.copyLock.unlock();
   }

   public void uploadGlobalGeomBuffersToGPU() {
      GpuDevice device = RenderSystem.getDevice();

      try (StagingBuffer.Uploader uploader = this.stagingBuffer.startUploading(device.createCommandEncoder())) {
         for(SectionUberBuffers buffers : this.chunkUberBuffers.values()) {
            boolean performedBufferResize = buffers.vertexBuffer.uploadStagedAllocations(device, uploader);
            buffers.indexBuffer.uploadStagedAllocations(device, uploader);
            if (performedBufferResize) {
               break;
            }
         }
      }

   }

   public void rebuildSectionSync(final RenderSection section, final RenderRegionCache cache) {
      section.compileSync(cache);
   }

   public void schedule(final RenderSection.CompileTask task) {
      if (!this.closed) {
         this.compileQueue.add(task);
         this.executor.execute(this::runTask);
      }
   }

   public void clearCompileQueue() {
      this.compileQueue.clear();
   }

   public boolean isQueueEmpty() {
      return this.compileQueue.size() == 0;
   }

   public void dispose() {
      this.closed = true;
      this.clearCompileQueue();
      this.copyLock.lock();

      try {
         for(SectionUberBuffers buffers : this.chunkUberBuffers.values()) {
            buffers.vertexBuffer.close();
            buffers.indexBuffer.close();
         }

         this.stagingBuffer.close();
      } finally {
         this.copyLock.unlock();
      }

   }

   @VisibleForDebug
   public String getStats() {
      return String.format(Locale.ROOT, "pC: %03d, aB: %02d", this.compileQueue.size(), this.bufferPool.getFreeBufferCount());
   }

   @VisibleForDebug
   public int getCompileQueueSize() {
      return this.compileQueue.size();
   }

   @VisibleForDebug
   public int getFreeBufferCount() {
      return this.bufferPool.getFreeBufferCount();
   }

   public static record RenderSectionBufferSlice(GpuBuffer vertexBuffer, long vertexBufferOffset, @Nullable GpuBuffer indexBuffer, long indexBufferOffset) {
      public RenderSectionBufferSlice {
         super();
      }
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
         SectionMesh mesh = (SectionMesh)this.sectionMesh.getAndSet(CompiledSectionMesh.UNCOMPILED);
         SectionRenderDispatcher.this.copyLock.lock();

         try {
            this.releaseSectionMesh(mesh);
         } finally {
            SectionRenderDispatcher.this.copyLock.unlock();
         }

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

      private SectionMesh setSectionMesh(final SectionMesh sectionMesh) {
         SectionMesh oldMesh = (SectionMesh)this.sectionMesh.getAndSet(sectionMesh);
         SectionRenderDispatcher.this.renderer.addRecentlyCompiledSection(this);
         if (this.uploadedTime == 0L) {
            this.uploadedTime = Util.getMillis();
         }

         return oldMesh;
      }

      private void releaseSectionMesh(final SectionMesh oldMesh) {
         oldMesh.close();

         for(SectionUberBuffers buffers : SectionRenderDispatcher.this.chunkUberBuffers.values()) {
            buffers.vertexBuffer.removeAllocation(oldMesh);
            buffers.indexBuffer.removeAllocation(oldMesh);
         }

      }

      private VertexSorting createVertexSorting(final SectionPos sectionPos, final Vec3 cameraPos) {
         return VertexSorting.byDistance((float)(cameraPos.x - (double)sectionPos.minBlockX()), (float)(cameraPos.y - (double)sectionPos.minBlockY()), (float)(cameraPos.z - (double)sectionPos.minBlockZ()));
      }

      private void checkSectionMesh(final CompiledSectionMesh compiledSectionMesh) {
         boolean allBuffersUpdated = true;

         for(ChunkSectionLayer layer : ChunkSectionLayer.values()) {
            SectionMesh.SectionDraw draw = compiledSectionMesh.getSectionDraw(layer);
            if (draw != null) {
               allBuffersUpdated &= compiledSectionMesh.isIndexBufferUploaded(layer);
               allBuffersUpdated &= compiledSectionMesh.isVertexBufferUploaded(layer);
            }
         }

         if (allBuffersUpdated && this.sectionMesh.get() != compiledSectionMesh) {
            SectionMesh oldMesh = this.setSectionMesh(compiledSectionMesh);
            this.releaseSectionMesh(oldMesh);
         }

      }

      void vertexBufferUploadCallback(final CompiledSectionMesh sectionMesh, final ChunkSectionLayer layer) {
         sectionMesh.setVertexBufferUploaded(layer);
         this.checkSectionMesh(sectionMesh);
      }

      void indexBufferUploadCallback(final CompiledSectionMesh sectionMesh, final ChunkSectionLayer layer, final boolean sortedIndexBuffer) {
         sectionMesh.setIndexBufferUploaded(layer);
         if (!sortedIndexBuffer) {
            this.checkSectionMesh(sectionMesh);
         }

      }

      private boolean addSectionBuffersToUberBuffer(final ChunkSectionLayer layer, final CompiledSectionMesh key, final @Nullable ByteBuffer vertexBuffer, final @Nullable ByteBuffer indexBuffer) {
         boolean success = true;
         SectionRenderDispatcher.this.copyLock.lock();

         try {
            SectionMesh.SectionDraw draw = key.getSectionDraw(layer);
            if (draw != null) {
               SectionUberBuffers sectionBuffers = (SectionUberBuffers)SectionRenderDispatcher.this.chunkUberBuffers.get(layer);

               assert sectionBuffers != null;

               if (vertexBuffer != null) {
                  UberGpuBuffer.UploadCallback<CompiledSectionMesh> callback = (mesh) -> this.vertexBufferUploadCallback(mesh, layer);
                  success &= sectionBuffers.vertexBuffer.addAllocation(key, callback, vertexBuffer);
               }

               if (indexBuffer != null) {
                  boolean sortedIndexBuffer = vertexBuffer == null;
                  UberGpuBuffer.UploadCallback<CompiledSectionMesh> callback = (mesh) -> this.indexBufferUploadCallback(mesh, layer, sortedIndexBuffer);
                  success &= sectionBuffers.indexBuffer.addAllocation(key, callback, indexBuffer);
               } else {
                  key.setIndexBufferUploaded(layer);
               }
            }

            if (!success && RenderSystem.isOnRenderThread()) {
               SectionRenderDispatcher.this.uploadGlobalGeomBuffersToGPU();
            }
         } finally {
            SectionRenderDispatcher.this.copyLock.unlock();
         }

         return success;
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

         public CompileTask.SectionTaskResult doTask(final SectionBufferBuilderPack buffers) {
            if (this.isCancelled.get()) {
               return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.CANCELLED;
            } else {
               long sectionNode = RenderSection.this.sectionNode;
               SectionPos sectionPos = SectionPos.of(sectionNode);
               if (this.isCancelled.get()) {
                  return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.CANCELLED;
               } else {
                  Vec3 cameraPos = (Vec3)SectionRenderDispatcher.this.cameraPosition.get();

                  SectionCompiler.Results results;
                  try (Zone ignored = Profiler.get().zone("Compile Section")) {
                     results = SectionRenderDispatcher.this.sectionCompiler.compile(sectionPos, this.region, RenderSection.this.createVertexSorting(sectionPos, cameraPos), buffers);
                  }

                  TranslucencyPointOfView translucencyPointOfView = TranslucencyPointOfView.of(cameraPos, sectionNode);
                  CompiledSectionMesh compiledSectionMesh = new CompiledSectionMesh(translucencyPointOfView, results);
                  if (results.renderedLayers.isEmpty()) {
                     SectionMesh oldMesh = RenderSection.this.setSectionMesh(compiledSectionMesh);
                     SectionRenderDispatcher.this.copyLock.lock();

                     try {
                        RenderSection.this.releaseSectionMesh(oldMesh);
                     } finally {
                        SectionRenderDispatcher.this.copyLock.unlock();
                     }

                     return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.SUCCESSFUL;
                  } else {
                     for(Map.Entry<ChunkSectionLayer, MeshData> entry : results.renderedLayers.entrySet()) {
                        MeshData meshData = (MeshData)entry.getValue();
                        boolean success = false;

                        while(!success) {
                           if (this.isCancelled.get()) {
                              results.release();
                              SectionRenderDispatcher.this.copyLock.lock();

                              try {
                                 RenderSection.this.releaseSectionMesh(compiledSectionMesh);
                              } finally {
                                 SectionRenderDispatcher.this.copyLock.unlock();
                              }

                              return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.CANCELLED;
                           }

                           success = RenderSection.this.addSectionBuffersToUberBuffer((ChunkSectionLayer)entry.getKey(), compiledSectionMesh, meshData.vertexBuffer(), meshData.indexBuffer());
                           if (!success && !RenderSystem.isOnRenderThread()) {
                              Thread.onSpinWait();
                           }
                        }

                        meshData.close();
                     }

                     return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.SUCCESSFUL;
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

         public CompileTask.SectionTaskResult doTask(final SectionBufferBuilderPack buffers) {
            if (this.isCancelled.get()) {
               return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.CANCELLED;
            } else {
               MeshData.SortState state = this.compiledSectionMesh.getTransparencyState();
               if (state != null && !this.compiledSectionMesh.isEmpty(ChunkSectionLayer.TRANSLUCENT)) {
                  Vec3 cameraPos = (Vec3)SectionRenderDispatcher.this.cameraPosition.get();
                  long sectionNode = RenderSection.this.sectionNode;
                  VertexSorting vertexSorting = RenderSection.this.createVertexSorting(SectionPos.of(sectionNode), cameraPos);
                  TranslucencyPointOfView translucencyPointOfView = TranslucencyPointOfView.of(cameraPos, sectionNode);
                  if (!this.compiledSectionMesh.isDifferentPointOfView(translucencyPointOfView) && !translucencyPointOfView.isAxisAligned()) {
                     return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.CANCELLED;
                  } else {
                     ByteBufferBuilder.Result indexBuffer = state.buildSortedIndexBuffer(buffers.buffer(ChunkSectionLayer.TRANSLUCENT), vertexSorting);
                     if (indexBuffer == null) {
                        return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.CANCELLED;
                     } else {
                        boolean success = false;

                        while(!success) {
                           if (this.isCancelled.get()) {
                              indexBuffer.close();
                              return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.CANCELLED;
                           }

                           success = RenderSection.this.addSectionBuffersToUberBuffer(ChunkSectionLayer.TRANSLUCENT, this.compiledSectionMesh, (ByteBuffer)null, indexBuffer.byteBuffer());
                           if (!success && !RenderSystem.isOnRenderThread()) {
                              Thread.onSpinWait();
                           }
                        }

                        indexBuffer.close();
                        this.compiledSectionMesh.setTranslucencyPointOfView(translucencyPointOfView);
                        return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.SUCCESSFUL;
                     }
                  }
               } else {
                  return SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult.CANCELLED;
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

         public abstract SectionTaskResult doTask(final SectionBufferBuilderPack buffers);

         public abstract void cancel();

         protected abstract String name();

         public boolean isRecompile() {
            return this.isRecompile;
         }

         public BlockPos getRenderOrigin() {
            return RenderSection.this.renderOrigin;
         }

         public static enum SectionTaskResult {
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
   }

   private static record SectionUberBuffers(UberGpuBuffer<SectionMesh> vertexBuffer, UberGpuBuffer<SectionMesh> indexBuffer) {
      private SectionUberBuffers {
         super();
      }
   }
}
