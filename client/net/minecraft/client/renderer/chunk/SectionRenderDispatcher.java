package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
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
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
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
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SectionRenderDispatcher {
   private static final int MAX_HIGH_PRIORITY_QUOTA = 2;
   private final PriorityBlockingQueue<RenderSection.CompileTask> toBatchHighPriority = Queues.newPriorityBlockingQueue();
   private final Queue<RenderSection.CompileTask> toBatchLowPriority = Queues.newLinkedBlockingDeque();
   private int highPriorityQuota = 2;
   private final Queue<Runnable> toUpload = Queues.newConcurrentLinkedQueue();
   final SectionBufferBuilderPack fixedBuffers;
   private final SectionBufferBuilderPool bufferPool;
   private volatile int toBatchCount;
   private volatile boolean closed;
   private final ProcessorMailbox<Runnable> mailbox;
   private final Executor executor;
   ClientLevel level;
   final LevelRenderer renderer;
   private Vec3 camera;
   final SectionCompiler sectionCompiler;

   public SectionRenderDispatcher(ClientLevel var1, LevelRenderer var2, Executor var3, RenderBuffers var4, BlockRenderDispatcher var5, BlockEntityRenderDispatcher var6) {
      super();
      this.camera = Vec3.ZERO;
      this.level = var1;
      this.renderer = var2;
      this.fixedBuffers = var4.fixedBufferPack();
      this.bufferPool = var4.sectionBufferPool();
      this.executor = var3;
      this.mailbox = ProcessorMailbox.create(var3, "Section Renderer");
      this.mailbox.tell(this::runTask);
      this.sectionCompiler = new SectionCompiler(var5, var6);
   }

   public void setLevel(ClientLevel var1) {
      this.level = var1;
   }

   private void runTask() {
      if (!this.closed && !this.bufferPool.isEmpty()) {
         RenderSection.CompileTask var1 = this.pollTask();
         if (var1 != null) {
            SectionBufferBuilderPack var2 = (SectionBufferBuilderPack)Objects.requireNonNull(this.bufferPool.acquire());
            this.toBatchCount = this.toBatchHighPriority.size() + this.toBatchLowPriority.size();
            CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName(var1.name(), () -> {
               return var1.doTask(var2);
            }), this.executor).thenCompose((var0) -> {
               return var0;
            }).whenComplete((var2x, var3) -> {
               if (var3 != null) {
                  Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var3, "Batching sections"));
               } else {
                  this.mailbox.tell(() -> {
                     if (var2x == SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL) {
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

   @Nullable
   private RenderSection.CompileTask pollTask() {
      RenderSection.CompileTask var1;
      if (this.highPriorityQuota <= 0) {
         var1 = (RenderSection.CompileTask)this.toBatchLowPriority.poll();
         if (var1 != null) {
            this.highPriorityQuota = 2;
            return var1;
         }
      }

      var1 = (RenderSection.CompileTask)this.toBatchHighPriority.poll();
      if (var1 != null) {
         --this.highPriorityQuota;
         return var1;
      } else {
         this.highPriorityQuota = 2;
         return (RenderSection.CompileTask)this.toBatchLowPriority.poll();
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
         this.mailbox.tell(() -> {
            if (!this.closed) {
               if (var1.isHighPriority) {
                  this.toBatchHighPriority.offer(var1);
               } else {
                  this.toBatchLowPriority.offer(var1);
               }

               this.toBatchCount = this.toBatchHighPriority.size() + this.toBatchLowPriority.size();
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
               var2.bind();
               var2.upload(var1);
               VertexBuffer.unbind();
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
               var2.bind();
               var2.uploadIndexBuffer(var1);
               VertexBuffer.unbind();
            }
         };
         Queue var10001 = this.toUpload;
         Objects.requireNonNull(var10001);
         return CompletableFuture.runAsync(var10000, var10001::add);
      }
   }

   private void clearBatchQueue() {
      RenderSection.CompileTask var1;
      while(!this.toBatchHighPriority.isEmpty()) {
         var1 = (RenderSection.CompileTask)this.toBatchHighPriority.poll();
         if (var1 != null) {
            var1.cancel();
         }
      }

      while(!this.toBatchLowPriority.isEmpty()) {
         var1 = (RenderSection.CompileTask)this.toBatchLowPriority.poll();
         if (var1 != null) {
            var1.cancel();
         }
      }

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
      private final AtomicInteger initialCompilationCancelCount;
      @Nullable
      private RebuildTask lastRebuildTask;
      @Nullable
      private ResortTransparencyTask lastResortTransparencyTask;
      private final Set<BlockEntity> globalBlockEntities;
      private final Map<RenderType, VertexBuffer> buffers;
      private AABB bb;
      private boolean dirty;
      final BlockPos.MutableBlockPos origin;
      private final BlockPos.MutableBlockPos[] relativeOrigins;
      private boolean playerChanged;

      public RenderSection(final int var2, final int var3, final int var4, final int var5) {
         super();
         this.compiled = new AtomicReference(SectionRenderDispatcher.CompiledSection.UNCOMPILED);
         this.initialCompilationCancelCount = new AtomicInteger(0);
         this.globalBlockEntities = Sets.newHashSet();
         this.buffers = (Map)RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((var0) -> {
            return var0;
         }, (var0) -> {
            return new VertexBuffer(VertexBuffer.Usage.STATIC);
         }));
         this.dirty = true;
         this.origin = new BlockPos.MutableBlockPos(-1, -1, -1);
         this.relativeOrigins = (BlockPos.MutableBlockPos[])Util.make(new BlockPos.MutableBlockPos[6], (var0) -> {
            for(int var1 = 0; var1 < var0.length; ++var1) {
               var0[var1] = new BlockPos.MutableBlockPos();
            }

         });
         this.index = var2;
         this.setOrigin(var3, var4, var5);
      }

      private boolean doesChunkExistAt(BlockPos var1) {
         return SectionRenderDispatcher.this.level.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()), ChunkStatus.FULL, false) != null;
      }

      public boolean hasAllNeighbors() {
         boolean var1 = true;
         if (!(this.getDistToPlayerSqr() > 576.0)) {
            return true;
         } else {
            return this.doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()]);
         }
      }

      public AABB getBoundingBox() {
         return this.bb;
      }

      public VertexBuffer getBuffer(RenderType var1) {
         return (VertexBuffer)this.buffers.get(var1);
      }

      public void setOrigin(int var1, int var2, int var3) {
         this.reset();
         this.origin.set(var1, var2, var3);
         this.bb = new AABB((double)var1, (double)var2, (double)var3, (double)(var1 + 16), (double)(var2 + 16), (double)(var3 + 16));
         Direction[] var4 = Direction.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Direction var7 = var4[var6];
            this.relativeOrigins[var7.ordinal()].set(this.origin).move(var7, 16);
         }

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
         this.dirty = true;
      }

      public void releaseBuffers() {
         this.reset();
         this.buffers.values().forEach(VertexBuffer::close);
      }

      public BlockPos getOrigin() {
         return this.origin;
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

      public BlockPos getRelativeOrigin(Direction var1) {
         return this.relativeOrigins[var1.ordinal()];
      }

      public boolean resortTransparency(RenderType var1, SectionRenderDispatcher var2) {
         CompiledSection var3 = this.getCompiled();
         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
         }

         if (!var3.hasBlocks.contains(var1)) {
            return false;
         } else {
            this.lastResortTransparencyTask = new ResortTransparencyTask(this.getDistToPlayerSqr(), var3);
            var2.schedule(this.lastResortTransparencyTask);
            return true;
         }
      }

      protected boolean cancelTasks() {
         boolean var1 = false;
         if (this.lastRebuildTask != null) {
            this.lastRebuildTask.cancel();
            this.lastRebuildTask = null;
            var1 = true;
         }

         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
            this.lastResortTransparencyTask = null;
         }

         return var1;
      }

      public CompileTask createCompileTask(RenderRegionCache var1) {
         boolean var2 = this.cancelTasks();
         RenderChunkRegion var3 = var1.createRegion(SectionRenderDispatcher.this.level, SectionPos.of((BlockPos)this.origin));
         boolean var4 = this.compiled.get() == SectionRenderDispatcher.CompiledSection.UNCOMPILED;
         if (var4 && var2) {
            this.initialCompilationCancelCount.incrementAndGet();
         }

         this.lastRebuildTask = new RebuildTask(this.getDistToPlayerSqr(), var3, !var4 || this.initialCompilationCancelCount.get() > 2);
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

      public boolean isAxisAlignedWith(int var1, int var2, int var3) {
         BlockPos var4 = this.getOrigin();
         return var1 == SectionPos.blockToSectionCoord(var4.getX()) || var3 == SectionPos.blockToSectionCoord(var4.getZ()) || var2 == SectionPos.blockToSectionCoord(var4.getY());
      }

      void setCompiled(CompiledSection var1) {
         this.compiled.set(var1);
         this.initialCompilationCancelCount.set(0);
         SectionRenderDispatcher.this.renderer.addRecentlyCompiledSection(this);
      }

      VertexSorting createVertexSorting() {
         Vec3 var1 = SectionRenderDispatcher.this.getCameraPosition();
         return VertexSorting.byDistance((float)(var1.x - (double)this.origin.getX()), (float)(var1.y - (double)this.origin.getY()), (float)(var1.z - (double)this.origin.getZ()));
      }

      private class ResortTransparencyTask extends CompileTask {
         private final CompiledSection compiledSection;

         public ResortTransparencyTask(final double var2, final CompiledSection var4) {
            super(RenderSection.this, var2, true);
            this.compiledSection = var4;
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
                  ByteBufferBuilder.Result var4 = var2.buildSortedIndexBuffer(var1.buffer(RenderType.translucent()), var3);
                  if (var4 == null) {
                     return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                  } else if (this.isCancelled.get()) {
                     var4.close();
                     return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                  } else {
                     CompletableFuture var5 = SectionRenderDispatcher.this.uploadSectionIndexBuffer(var4, RenderSection.this.getBuffer(RenderType.translucent())).thenApply((var0) -> {
                        return SectionRenderDispatcher.SectionTaskResult.CANCELLED;
                     });
                     return var5.handle((var1x, var2x) -> {
                        if (var2x != null && !(var2x instanceof CancellationException) && !(var2x instanceof InterruptedException)) {
                           Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var2x, "Rendering section"));
                        }

                        return this.isCancelled.get() ? SectionRenderDispatcher.SectionTaskResult.CANCELLED : SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL;
                     });
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

      private abstract class CompileTask implements Comparable<CompileTask> {
         protected final double distAtCreation;
         protected final AtomicBoolean isCancelled = new AtomicBoolean(false);
         protected final boolean isHighPriority;

         public CompileTask(final RenderSection var1, final double var2, final boolean var4) {
            super();
            this.distAtCreation = var2;
            this.isHighPriority = var4;
         }

         public abstract CompletableFuture<SectionTaskResult> doTask(SectionBufferBuilderPack var1);

         public abstract void cancel();

         protected abstract String name();

         public int compareTo(CompileTask var1) {
            return Doubles.compare(this.distAtCreation, var1.distAtCreation);
         }

         // $FF: synthetic method
         public int compareTo(final Object var1) {
            return this.compareTo((CompileTask)var1);
         }
      }

      private class RebuildTask extends CompileTask {
         @Nullable
         protected RenderChunkRegion region;

         public RebuildTask(final double var2, @Nullable final RenderChunkRegion var4, final boolean var5) {
            super(RenderSection.this, var2, var5);
            this.region = var4;
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
                  SectionCompiler.Results var4 = SectionRenderDispatcher.this.sectionCompiler.compile(var3, var2, RenderSection.this.createVertexSorting(), var1);
                  RenderSection.this.updateGlobalBlockEntities(var4.globalBlockEntities);
                  if (this.isCancelled.get()) {
                     var4.release();
                     return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                  } else {
                     CompiledSection var5 = new CompiledSection();
                     var5.visibilitySet = var4.visibilitySet;
                     var5.renderableBlockEntities.addAll(var4.blockEntities);
                     var5.transparencyState = var4.transparencyState;
                     ArrayList var6 = new ArrayList(var4.renderedLayers.size());
                     var4.renderedLayers.forEach((var3x, var4x) -> {
                        var6.add(SectionRenderDispatcher.this.uploadSectionLayer(var4x, RenderSection.this.getBuffer(var3x)));
                        var5.hasBlocks.add(var3x);
                     });
                     return Util.sequenceFailFast(var6).handle((var2x, var3x) -> {
                        if (var3x != null && !(var3x instanceof CancellationException) && !(var3x instanceof InterruptedException)) {
                           Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var3x, "Rendering section"));
                        }

                        if (this.isCancelled.get()) {
                           return SectionRenderDispatcher.SectionTaskResult.CANCELLED;
                        } else {
                           RenderSection.this.setCompiled(var5);
                           return SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL;
                        }
                     });
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

      public boolean hasNoRenderableLayers() {
         return this.hasBlocks.isEmpty();
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
