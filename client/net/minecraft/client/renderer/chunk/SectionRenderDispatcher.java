package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexSorting;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.TracingExecutor;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SectionRenderDispatcher {
   private final CompileTaskDynamicQueue compileQueue = new CompileTaskDynamicQueue();
   private final Queue<Runnable> toUpload = Queues.newConcurrentLinkedQueue();
   final SectionBufferBuilderPack fixedBuffers;
   private final SectionBufferBuilderPool bufferPool;
   private volatile int toBatchCount;
   private volatile boolean closed;
   private final ConsecutiveExecutor consecutiveExecutor;
   private final TracingExecutor executor;
   ClientLevel level;
   final LevelRenderer renderer;
   private Vec3 camera;
   final SectionCompiler sectionCompiler;

   public SectionRenderDispatcher(ClientLevel var1, LevelRenderer var2, TracingExecutor var3, RenderBuffers var4, BlockRenderDispatcher var5, BlockEntityRenderDispatcher var6) {
      super();
      this.camera = Vec3.ZERO;
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
         RenderSection.CompileTask var1 = this.compileQueue.poll(this.getCameraPosition());
         if (var1 != null) {
            SectionBufferBuilderPack var2 = (SectionBufferBuilderPack)Objects.requireNonNull(this.bufferPool.acquire());
            this.toBatchCount = this.compileQueue.size();
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

   public String getStats() {
      return String.format(Locale.ROOT, "pC: %03d, pU: %02d, aB: %02d", this.toBatchCount, this.toUpload.size(), this.bufferPool.getFreeBufferCount());
   }

   public int getToBatchCount() {
      return this.toBatchCount;
   }

   public int getToUpload() {
      return this.toUpload.size();
   }

   public int getFreeBufferCount() {
      return this.bufferPool.getFreeBufferCount();
   }

   public void setCamera(Vec3 var1) {
      this.camera = var1;
   }

   public Vec3 getCameraPosition() {
      return this.camera;
   }

   public void uploadAllPendingUploads() {
      Runnable var1;
      while((var1 = (Runnable)this.toUpload.poll()) != null) {
         var1.run();
      }

   }

   public void rebuildSectionSync(RenderSection var1, RenderRegionCache var2) {
      var1.compileSync(var2);
   }

   public void blockUntilClear() {
      this.clearBatchQueue();
   }

   public void schedule(RenderSection.CompileTask var1) {
      if (!this.closed) {
         this.consecutiveExecutor.schedule(() -> {
            if (!this.closed) {
               this.compileQueue.add(var1);
               this.toBatchCount = this.compileQueue.size();
               this.runTask();
            }
         });
      }
   }

   public CompletableFuture<Void> uploadSectionLayer(MeshData var1, VertexBuffer var2) {
      if (this.closed) {
         return CompletableFuture.completedFuture((Object)null);
      } else {
         Runnable var10000 = () -> {
            if (var2.isInvalid()) {
               var1.close();
            } else {
               try (Zone var2x = Profiler.get().zone("Upload Section Layer")) {
                  var2.bind();
                  var2.upload(var1);
                  VertexBuffer.unbind();
               }

            }
         };
         Queue var10001 = this.toUpload;
         Objects.requireNonNull(var10001);
         return CompletableFuture.runAsync(var10000, var10001::add);
      }
   }

   public CompletableFuture<Void> uploadSectionIndexBuffer(ByteBufferBuilder.Result var1, VertexBuffer var2) {
      if (this.closed) {
         return CompletableFuture.completedFuture((Object)null);
      } else {
         Runnable var10000 = () -> {
            if (var2.isInvalid()) {
               var1.close();
            } else {
               try (Zone var2x = Profiler.get().zone("Upload Section Indices")) {
                  var2.bind();
                  var2.uploadIndexBuffer(var1);
                  VertexBuffer.unbind();
               }

            }
         };
         Queue var10001 = this.toUpload;
         Objects.requireNonNull(var10001);
         return CompletableFuture.runAsync(var10000, var10001::add);
      }
   }

   private void clearBatchQueue() {
      this.compileQueue.clear();
      this.toBatchCount = 0;
   }

   public boolean isQueueEmpty() {
      return this.toBatchCount == 0 && this.toUpload.isEmpty();
   }

   public void dispose() {
      this.closed = true;
      this.clearBatchQueue();
      this.uploadAllPendingUploads();
   }

   public class RenderSection {
      public static final int SIZE = 16;
      public final int index;
      public final AtomicReference<CompiledSection> compiled;
      public final AtomicReference<TranslucencyPointOfView> pointOfView;
      @Nullable
      private RebuildTask lastRebuildTask;
      @Nullable
      private ResortTransparencyTask lastResortTransparencyTask;
      private final Set<BlockEntity> globalBlockEntities;
      private final Map<RenderType, VertexBuffer> buffers;
      private AABB bb;
      private boolean dirty;
      long sectionNode;
      final BlockPos.MutableBlockPos origin;
      private boolean playerChanged;

      public RenderSection(final int var2, final long var3) {
         super();
         this.compiled = new AtomicReference(SectionRenderDispatcher.CompiledSection.UNCOMPILED);
         this.pointOfView = new AtomicReference((Object)null);
         this.globalBlockEntities = Sets.newHashSet();
         this.buffers = (Map)RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((var0) -> var0, (var0) -> new VertexBuffer(BufferUsage.STATIC_WRITE)));
         this.dirty = true;
         this.sectionNode = SectionPos.asLong(-1, -1, -1);
         this.origin = new BlockPos.MutableBlockPos(-1, -1, -1);
         this.index = var2;
         this.setSectionNode(var3);
      }

      private boolean doesChunkExistAt(long var1) {
         ChunkAccess var3 = SectionRenderDispatcher.this.level.getChunk(SectionPos.x(var1), SectionPos.z(var1), ChunkStatus.FULL, false);
         return var3 != null && SectionRenderDispatcher.this.level.getLightEngine().lightOnInColumn(SectionPos.getZeroNode(var1));
      }

      public boolean hasAllNeighbors() {
         boolean var1 = true;
         if (!(this.getDistToPlayerSqr() > 576.0)) {
            return true;
         } else {
            return this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.WEST)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.NORTH)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.EAST)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, Direction.SOUTH)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, -1, 0, -1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, -1, 0, 1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, 1, 0, -1)) && this.doesChunkExistAt(SectionPos.offset(this.sectionNode, 1, 0, 1));
         }
      }

      public AABB getBoundingBox() {
         return this.bb;
      }

      public VertexBuffer getBuffer(RenderType var1) {
         return (VertexBuffer)this.buffers.get(var1);
      }

      public void setSectionNode(long var1) {
         this.reset();
         this.sectionNode = var1;
         int var3 = SectionPos.sectionToBlockCoord(SectionPos.x(var1));
         int var4 = SectionPos.sectionToBlockCoord(SectionPos.y(var1));
         int var5 = SectionPos.sectionToBlockCoord(SectionPos.z(var1));
         this.origin.set(var3, var4, var5);
         this.bb = new AABB((double)var3, (double)var4, (double)var5, (double)(var3 + 16), (double)(var4 + 16), (double)(var5 + 16));
      }

      protected double getDistToPlayerSqr() {
         Camera var1 = Minecraft.getInstance().gameRenderer.getMainCamera();
         double var2 = this.bb.minX + 8.0 - var1.getPosition().x;
         double var4 = this.bb.minY + 8.0 - var1.getPosition().y;
         double var6 = this.bb.minZ + 8.0 - var1.getPosition().z;
         return var2 * var2 + var4 * var4 + var6 * var6;
      }

      public CompiledSection getCompiled() {
         return (CompiledSection)this.compiled.get();
      }

      private void reset() {
         this.cancelTasks();
         this.compiled.set(SectionRenderDispatcher.CompiledSection.UNCOMPILED);
         this.pointOfView.set((Object)null);
         this.dirty = true;
      }

      public void releaseBuffers() {
         this.reset();
         this.buffers.values().forEach(VertexBuffer::close);
      }

      public BlockPos getOrigin() {
         return this.origin;
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
         this.lastResortTransparencyTask = new ResortTransparencyTask(this.getCompiled());
         var1.schedule(this.lastResortTransparencyTask);
      }

      public boolean hasTranslucentGeometry() {
         return this.getCompiled().hasBlocks.contains(RenderType.translucent());
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
         RenderChunkRegion var2 = var1.createRegion(SectionRenderDispatcher.this.level, SectionPos.of(this.sectionNode));
         boolean var3 = this.compiled.get() != SectionRenderDispatcher.CompiledSection.UNCOMPILED;
         this.lastRebuildTask = new RebuildTask(var2, var3);
         return this.lastRebuildTask;
      }

      public void rebuildSectionAsync(SectionRenderDispatcher var1, RenderRegionCache var2) {
         CompileTask var3 = this.createCompileTask(var2);
         var1.schedule(var3);
      }

      void updateGlobalBlockEntities(Collection<BlockEntity> var1) {
         HashSet var2 = Sets.newHashSet(var1);
         HashSet var3;
         synchronized(this.globalBlockEntities) {
            var3 = Sets.newHashSet(this.globalBlockEntities);
            var2.removeAll(this.globalBlockEntities);
            var3.removeAll(var1);
            this.globalBlockEntities.clear();
            this.globalBlockEntities.addAll(var1);
         }

         SectionRenderDispatcher.this.renderer.updateGlobalBlockEntities(var3, var2);
      }

      public void compileSync(RenderRegionCache var1) {
         CompileTask var2 = this.createCompileTask(var1);
         var2.doTask(SectionRenderDispatcher.this.fixedBuffers);
      }

      void setCompiled(CompiledSection var1) {
         this.compiled.set(var1);
         SectionRenderDispatcher.this.renderer.addRecentlyCompiledSection(this);
      }

      VertexSorting createVertexSorting() {
         Vec3 var1 = SectionRenderDispatcher.this.getCameraPosition();
         return VertexSorting.byDistance((float)(var1.x - (double)this.origin.getX()), (float)(var1.y - (double)this.origin.getY()), (float)(var1.z - (double)this.origin.getZ()));
      }

      class RebuildTask extends CompileTask {
         @Nullable
         protected volatile RenderChunkRegion region;

         public RebuildTask(@Nullable final RenderChunkRegion var2, final boolean var3) {
            super(var3);
            this.region = var2;
         }

         protected String name() {
            return "rend_chk_rebuild";
         }

         public CompletableFuture<SectionTaskResult> doTask(SectionBufferBuilderPack var1) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
            } else if (!RenderSection.this.hasAllNeighbors()) {
               this.cancel();
               return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
            } else {
               RenderChunkRegion var2 = this.region;
               this.region = null;
               if (var2 == null) {
                  RenderSection.this.setCompiled(SectionRenderDispatcher.CompiledSection.EMPTY);
                  return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL);
               } else {
                  SectionPos var3 = SectionPos.of((BlockPos)RenderSection.this.origin);
                  if (this.isCancelled.get()) {
                     return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                  } else {
                     SectionCompiler.Results var4;
                     try (Zone var5 = Profiler.get().zone("Compile Section")) {
                        var4 = SectionRenderDispatcher.this.sectionCompiler.compile(var3, var2, RenderSection.this.createVertexSorting(), var1);
                     }

                     TranslucencyPointOfView var10 = SectionRenderDispatcher.TranslucencyPointOfView.of(SectionRenderDispatcher.this.getCameraPosition(), RenderSection.this.sectionNode);
                     RenderSection.this.updateGlobalBlockEntities(var4.globalBlockEntities);
                     if (this.isCancelled.get()) {
                        var4.release();
                        return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                     } else {
                        CompiledSection var6 = new CompiledSection();
                        var6.visibilitySet = var4.visibilitySet;
                        var6.renderableBlockEntities.addAll(var4.blockEntities);
                        var6.transparencyState = var4.transparencyState;
                        ArrayList var7 = new ArrayList(var4.renderedLayers.size());
                        var4.renderedLayers.forEach((var3x, var4x) -> {
                           var7.add(SectionRenderDispatcher.this.uploadSectionLayer(var4x, RenderSection.this.getBuffer(var3x)));
                           var6.hasBlocks.add(var3x);
                        });
                        return Util.sequenceFailFast(var7).handle((var3x, var4x) -> {
                           if (var4x != null && !(var4x instanceof CancellationException) && !(var4x instanceof InterruptedException)) {
                              Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var4x, "Rendering section"));
                           }

                           if (this.isCancelled.get()) {
                              return SectionRenderDispatcher.SectionTaskResult.CANCELLED;
                           } else {
                              RenderSection.this.setCompiled(var6);
                              RenderSection.this.pointOfView.set(var10);
                              return SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL;
                           }
                        });
                     }
                  }
               }
            }
         }

         public void cancel() {
            this.region = null;
            if (this.isCancelled.compareAndSet(false, true)) {
               RenderSection.this.setDirty(false);
            }

         }
      }

      class ResortTransparencyTask extends CompileTask {
         private final CompiledSection compiledSection;

         public ResortTransparencyTask(final CompiledSection var2) {
            super(true);
            this.compiledSection = var2;
         }

         protected String name() {
            return "rend_chk_sort";
         }

         public CompletableFuture<SectionTaskResult> doTask(SectionBufferBuilderPack var1) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
            } else if (!RenderSection.this.hasAllNeighbors()) {
               this.isCancelled.set(true);
               return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
            } else {
               MeshData.SortState var2 = this.compiledSection.transparencyState;
               if (var2 != null && !this.compiledSection.isEmpty(RenderType.translucent())) {
                  VertexSorting var3 = RenderSection.this.createVertexSorting();
                  TranslucencyPointOfView var4 = SectionRenderDispatcher.TranslucencyPointOfView.of(SectionRenderDispatcher.this.getCameraPosition(), RenderSection.this.sectionNode);
                  if (var4.equals(RenderSection.this.pointOfView.get()) && !var4.isAxisAligned()) {
                     return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                  } else {
                     ByteBufferBuilder.Result var5 = var2.buildSortedIndexBuffer(var1.buffer(RenderType.translucent()), var3);
                     if (var5 == null) {
                        return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                     } else if (this.isCancelled.get()) {
                        var5.close();
                        return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                     } else {
                        CompletableFuture var6 = SectionRenderDispatcher.this.uploadSectionIndexBuffer(var5, RenderSection.this.getBuffer(RenderType.translucent())).thenApply((var0) -> SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                        return var6.handle((var2x, var3x) -> {
                           if (var3x != null && !(var3x instanceof CancellationException) && !(var3x instanceof InterruptedException)) {
                              Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var3x, "Rendering section"));
                           }

                           if (this.isCancelled.get()) {
                              return SectionRenderDispatcher.SectionTaskResult.CANCELLED;
                           } else {
                              RenderSection.this.pointOfView.set(var4);
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

         public BlockPos getOrigin() {
            return RenderSection.this.origin;
         }
      }
   }

   public static final class TranslucencyPointOfView {
      private int x;
      private int y;
      private int z;

      public TranslucencyPointOfView() {
         super();
      }

      public static TranslucencyPointOfView of(Vec3 var0, long var1) {
         return (new TranslucencyPointOfView()).set(var0, var1);
      }

      public TranslucencyPointOfView set(Vec3 var1, long var2) {
         this.x = getCoordinate(var1.x(), SectionPos.x(var2));
         this.y = getCoordinate(var1.y(), SectionPos.y(var2));
         this.z = getCoordinate(var1.z(), SectionPos.z(var2));
         return this;
      }

      private static int getCoordinate(double var0, int var2) {
         int var3 = SectionPos.blockToSectionCoord(var0) - var2;
         return Mth.clamp(var3, -1, 1);
      }

      public boolean isAxisAligned() {
         return this.x == 0 || this.y == 0 || this.z == 0;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof TranslucencyPointOfView)) {
            return false;
         } else {
            TranslucencyPointOfView var2 = (TranslucencyPointOfView)var1;
            return this.x == var2.x && this.y == var2.y && this.z == var2.z;
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

   public static class CompiledSection {
      public static final CompiledSection UNCOMPILED = new CompiledSection() {
         public boolean facesCanSeeEachother(Direction var1, Direction var2) {
            return false;
         }
      };
      public static final CompiledSection EMPTY = new CompiledSection() {
         public boolean facesCanSeeEachother(Direction var1, Direction var2) {
            return true;
         }
      };
      final Set<RenderType> hasBlocks = new ObjectArraySet(RenderType.chunkBufferLayers().size());
      final List<BlockEntity> renderableBlockEntities = Lists.newArrayList();
      VisibilitySet visibilitySet = new VisibilitySet();
      @Nullable
      MeshData.SortState transparencyState;

      public CompiledSection() {
         super();
      }

      public boolean hasRenderableLayers() {
         return !this.hasBlocks.isEmpty();
      }

      public boolean isEmpty(RenderType var1) {
         return !this.hasBlocks.contains(var1);
      }

      public List<BlockEntity> getRenderableBlockEntities() {
         return this.renderableBlockEntities;
      }

      public boolean facesCanSeeEachother(Direction var1, Direction var2) {
         return this.visibilitySet.visibilityBetween(var1, var2);
      }
   }
}
