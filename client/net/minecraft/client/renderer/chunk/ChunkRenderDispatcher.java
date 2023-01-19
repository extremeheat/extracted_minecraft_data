package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_WORKERS_32_BIT = 4;
   private static final VertexFormat VERTEX_FORMAT = DefaultVertexFormat.BLOCK;
   private static final int MAX_HIGH_PRIORITY_QUOTA = 2;
   private final PriorityBlockingQueue<ChunkRenderDispatcher.RenderChunk.ChunkCompileTask> toBatchHighPriority = Queues.newPriorityBlockingQueue();
   private final Queue<ChunkRenderDispatcher.RenderChunk.ChunkCompileTask> toBatchLowPriority = Queues.newLinkedBlockingDeque();
   private int highPriorityQuota = 2;
   private final Queue<ChunkBufferBuilderPack> freeBuffers;
   private final Queue<Runnable> toUpload = Queues.newConcurrentLinkedQueue();
   private volatile int toBatchCount;
   private volatile int freeBufferCount;
   final ChunkBufferBuilderPack fixedBuffers;
   private final ProcessorMailbox<Runnable> mailbox;
   private final Executor executor;
   ClientLevel level;
   final LevelRenderer renderer;
   private Vec3 camera = Vec3.ZERO;

   public ChunkRenderDispatcher(ClientLevel var1, LevelRenderer var2, Executor var3, boolean var4, ChunkBufferBuilderPack var5) {
      super();
      this.level = var1;
      this.renderer = var2;
      int var6 = Math.max(
         1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / (RenderType.chunkBufferLayers().stream().mapToInt(RenderType::bufferSize).sum() * 4) - 1
      );
      int var7 = Runtime.getRuntime().availableProcessors();
      int var8 = var4 ? var7 : Math.min(var7, 4);
      int var9 = Math.max(1, Math.min(var8, var6));
      this.fixedBuffers = var5;
      ArrayList var10 = Lists.newArrayListWithExpectedSize(var9);

      try {
         for(int var11 = 0; var11 < var9; ++var11) {
            var10.add(new ChunkBufferBuilderPack());
         }
      } catch (OutOfMemoryError var14) {
         LOGGER.warn("Allocated only {}/{} buffers", var10.size(), var9);
         int var12 = Math.min(var10.size() * 2 / 3, var10.size() - 1);

         for(int var13 = 0; var13 < var12; ++var13) {
            var10.remove(var10.size() - 1);
         }

         System.gc();
      }

      this.freeBuffers = Queues.newArrayDeque(var10);
      this.freeBufferCount = this.freeBuffers.size();
      this.executor = var3;
      this.mailbox = ProcessorMailbox.create(var3, "Chunk Renderer");
      this.mailbox.tell(this::runTask);
   }

   public void setLevel(ClientLevel var1) {
      this.level = var1;
   }

   private void runTask() {
      if (!this.freeBuffers.isEmpty()) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var1 = this.pollTask();
         if (var1 != null) {
            ChunkBufferBuilderPack var2 = this.freeBuffers.poll();
            this.toBatchCount = this.toBatchHighPriority.size() + this.toBatchLowPriority.size();
            this.freeBufferCount = this.freeBuffers.size();
            CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName(var1.name(), () -> var1.doTask(var2)), this.executor)
               .thenCompose(var0 -> var0)
               .whenComplete((var2x, var3) -> {
                  if (var3 != null) {
                     Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var3, "Batching chunks"));
                  } else {
                     this.mailbox.tell(() -> {
                        if (var2x == ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL) {
                           var2.clearAll();
                        } else {
                           var2.discardAll();
                        }
   
                        this.freeBuffers.add(var2);
                        this.freeBufferCount = this.freeBuffers.size();
                        this.runTask();
                     });
                  }
               });
         }
      }
   }

   @Nullable
   private ChunkRenderDispatcher.RenderChunk.ChunkCompileTask pollTask() {
      if (this.highPriorityQuota <= 0) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var1 = this.toBatchLowPriority.poll();
         if (var1 != null) {
            this.highPriorityQuota = 2;
            return var1;
         }
      }

      ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var2 = this.toBatchHighPriority.poll();
      if (var2 != null) {
         --this.highPriorityQuota;
         return var2;
      } else {
         this.highPriorityQuota = 2;
         return this.toBatchLowPriority.poll();
      }
   }

   public String getStats() {
      return String.format(Locale.ROOT, "pC: %03d, pU: %02d, aB: %02d", this.toBatchCount, this.toUpload.size(), this.freeBufferCount);
   }

   public int getToBatchCount() {
      return this.toBatchCount;
   }

   public int getToUpload() {
      return this.toUpload.size();
   }

   public int getFreeBufferCount() {
      return this.freeBufferCount;
   }

   public void setCamera(Vec3 var1) {
      this.camera = var1;
   }

   public Vec3 getCameraPosition() {
      return this.camera;
   }

   public void uploadAllPendingUploads() {
      Runnable var1;
      while((var1 = this.toUpload.poll()) != null) {
         var1.run();
      }
   }

   public void rebuildChunkSync(ChunkRenderDispatcher.RenderChunk var1, RenderRegionCache var2) {
      var1.compileSync(var2);
   }

   public void blockUntilClear() {
      this.clearBatchQueue();
   }

   public void schedule(ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var1) {
      this.mailbox.tell(() -> {
         if (var1.isHighPriority) {
            this.toBatchHighPriority.offer(var1);
         } else {
            this.toBatchLowPriority.offer(var1);
         }

         this.toBatchCount = this.toBatchHighPriority.size() + this.toBatchLowPriority.size();
         this.runTask();
      });
   }

   public CompletableFuture<Void> uploadChunkLayer(BufferBuilder.RenderedBuffer var1, VertexBuffer var2) {
      return CompletableFuture.runAsync(() -> {
         if (!var2.isInvalid()) {
            var2.bind();
            var2.upload(var1);
            VertexBuffer.unbind();
         }
      }, this.toUpload::add);
   }

   private void clearBatchQueue() {
      while(!this.toBatchHighPriority.isEmpty()) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var1 = this.toBatchHighPriority.poll();
         if (var1 != null) {
            var1.cancel();
         }
      }

      while(!this.toBatchLowPriority.isEmpty()) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var2 = this.toBatchLowPriority.poll();
         if (var2 != null) {
            var2.cancel();
         }
      }

      this.toBatchCount = 0;
   }

   public boolean isQueueEmpty() {
      return this.toBatchCount == 0 && this.toUpload.isEmpty();
   }

   public void dispose() {
      this.clearBatchQueue();
      this.mailbox.close();
      this.freeBuffers.clear();
   }

   static enum ChunkTaskResult {
      SUCCESSFUL,
      CANCELLED;

      private ChunkTaskResult() {
      }
   }

   public static class CompiledChunk {
      public static final ChunkRenderDispatcher.CompiledChunk UNCOMPILED = new ChunkRenderDispatcher.CompiledChunk() {
         @Override
         public boolean facesCanSeeEachother(Direction var1, Direction var2) {
            return false;
         }
      };
      final Set<RenderType> hasBlocks = new ObjectArraySet(RenderType.chunkBufferLayers().size());
      final List<BlockEntity> renderableBlockEntities = Lists.newArrayList();
      VisibilitySet visibilitySet = new VisibilitySet();
      @Nullable
      BufferBuilder.SortState transparencyState;

      public CompiledChunk() {
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

   public class RenderChunk {
      public static final int SIZE = 16;
      public final int index;
      public final AtomicReference<ChunkRenderDispatcher.CompiledChunk> compiled = new AtomicReference<>(ChunkRenderDispatcher.CompiledChunk.UNCOMPILED);
      final AtomicInteger initialCompilationCancelCount = new AtomicInteger(0);
      @Nullable
      private ChunkRenderDispatcher.RenderChunk.RebuildTask lastRebuildTask;
      @Nullable
      private ChunkRenderDispatcher.RenderChunk.ResortTransparencyTask lastResortTransparencyTask;
      private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
      private final Map<RenderType, VertexBuffer> buffers = RenderType.chunkBufferLayers()
         .stream()
         .collect(Collectors.toMap(var0 -> var0, var0 -> new VertexBuffer()));
      private AABB bb;
      private boolean dirty = true;
      final BlockPos.MutableBlockPos origin = new BlockPos.MutableBlockPos(-1, -1, -1);
      private final BlockPos.MutableBlockPos[] relativeOrigins = Util.make(new BlockPos.MutableBlockPos[6], var0 -> {
         for(int var1x = 0; var1x < var0.length; ++var1x) {
            var0[var1x] = new BlockPos.MutableBlockPos();
         }
      });
      private boolean playerChanged;

      public RenderChunk(int var2, int var3, int var4, int var5) {
         super();
         this.index = var2;
         this.setOrigin(var3, var4, var5);
      }

      private boolean doesChunkExistAt(BlockPos var1) {
         return ChunkRenderDispatcher.this.level
               .getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()), ChunkStatus.FULL, false)
            != null;
      }

      public boolean hasAllNeighbors() {
         boolean var1 = true;
         if (!(this.getDistToPlayerSqr() > 576.0)) {
            return true;
         } else {
            return this.doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()])
               && this.doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()])
               && this.doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()])
               && this.doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()]);
         }
      }

      public AABB getBoundingBox() {
         return this.bb;
      }

      public VertexBuffer getBuffer(RenderType var1) {
         return this.buffers.get(var1);
      }

      public void setOrigin(int var1, int var2, int var3) {
         this.reset();
         this.origin.set(var1, var2, var3);
         this.bb = new AABB((double)var1, (double)var2, (double)var3, (double)(var1 + 16), (double)(var2 + 16), (double)(var3 + 16));

         for(Direction var7 : Direction.values()) {
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

      void beginLayer(BufferBuilder var1) {
         var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
      }

      public ChunkRenderDispatcher.CompiledChunk getCompiledChunk() {
         return this.compiled.get();
      }

      private void reset() {
         this.cancelTasks();
         this.compiled.set(ChunkRenderDispatcher.CompiledChunk.UNCOMPILED);
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

      public boolean resortTransparency(RenderType var1, ChunkRenderDispatcher var2) {
         ChunkRenderDispatcher.CompiledChunk var3 = this.getCompiledChunk();
         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
         }

         if (!var3.hasBlocks.contains(var1)) {
            return false;
         } else {
            this.lastResortTransparencyTask = new ChunkRenderDispatcher.RenderChunk.ResortTransparencyTask(this.getDistToPlayerSqr(), var3);
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

      public ChunkRenderDispatcher.RenderChunk.ChunkCompileTask createCompileTask(RenderRegionCache var1) {
         boolean var2 = this.cancelTasks();
         BlockPos var3 = this.origin.immutable();
         boolean var4 = true;
         RenderChunkRegion var5 = var1.createRegion(ChunkRenderDispatcher.this.level, var3.offset(-1, -1, -1), var3.offset(16, 16, 16), 1);
         boolean var6 = this.compiled.get() == ChunkRenderDispatcher.CompiledChunk.UNCOMPILED;
         if (var6 && var2) {
            this.initialCompilationCancelCount.incrementAndGet();
         }

         this.lastRebuildTask = new ChunkRenderDispatcher.RenderChunk.RebuildTask(
            this.getDistToPlayerSqr(), var5, !var6 || this.initialCompilationCancelCount.get() > 2
         );
         return this.lastRebuildTask;
      }

      public void rebuildChunkAsync(ChunkRenderDispatcher var1, RenderRegionCache var2) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var3 = this.createCompileTask(var2);
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

         ChunkRenderDispatcher.this.renderer.updateGlobalBlockEntities(var3, var2);
      }

      public void compileSync(RenderRegionCache var1) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var2 = this.createCompileTask(var1);
         var2.doTask(ChunkRenderDispatcher.this.fixedBuffers);
      }

      abstract class ChunkCompileTask implements Comparable<ChunkRenderDispatcher.RenderChunk.ChunkCompileTask> {
         protected final double distAtCreation;
         protected final AtomicBoolean isCancelled = new AtomicBoolean(false);
         protected final boolean isHighPriority;

         public ChunkCompileTask(double var2, boolean var4) {
            super();
            this.distAtCreation = var2;
            this.isHighPriority = var4;
         }

         public abstract CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(ChunkBufferBuilderPack var1);

         public abstract void cancel();

         protected abstract String name();

         public int compareTo(ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var1) {
            return Doubles.compare(this.distAtCreation, var1.distAtCreation);
         }
      }

      class RebuildTask extends ChunkRenderDispatcher.RenderChunk.ChunkCompileTask {
         @Nullable
         protected RenderChunkRegion region;

         public RebuildTask(double var2, @Nullable RenderChunkRegion var4, boolean var5) {
            super(var2, var5);
            this.region = var4;
         }

         @Override
         protected String name() {
            return "rend_chk_rebuild";
         }

         @Override
         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(ChunkBufferBuilderPack var1) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!RenderChunk.this.hasAllNeighbors()) {
               this.region = null;
               RenderChunk.this.setDirty(false);
               this.isCancelled.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vec3 var2 = ChunkRenderDispatcher.this.getCameraPosition();
               float var3 = (float)var2.x;
               float var4 = (float)var2.y;
               float var5 = (float)var2.z;
               ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults var6 = this.compile(var3, var4, var5, var1);
               RenderChunk.this.updateGlobalBlockEntities(var6.globalBlockEntities);
               if (this.isCancelled.get()) {
                  var6.renderedLayers.values().forEach(BufferBuilder.RenderedBuffer::release);
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               } else {
                  ChunkRenderDispatcher.CompiledChunk var7 = new ChunkRenderDispatcher.CompiledChunk();
                  var7.visibilitySet = var6.visibilitySet;
                  var7.renderableBlockEntities.addAll(var6.blockEntities);
                  var7.transparencyState = var6.transparencyState;
                  ArrayList var8 = Lists.newArrayList();
                  var6.renderedLayers.forEach((var3x, var4x) -> {
                     var8.add(ChunkRenderDispatcher.this.uploadChunkLayer(var4x, RenderChunk.this.getBuffer(var3x)));
                     var7.hasBlocks.add(var3x);
                  });
                  return Util.sequenceFailFast(var8).handle((var2x, var3x) -> {
                     if (var3x != null && !(var3x instanceof CancellationException) && !(var3x instanceof InterruptedException)) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var3x, "Rendering chunk"));
                     }

                     if (this.isCancelled.get()) {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     } else {
                        RenderChunk.this.compiled.set(var7);
                        RenderChunk.this.initialCompilationCancelCount.set(0);
                        ChunkRenderDispatcher.this.renderer.addRecentlyCompiledChunk(RenderChunk.this);
                        return ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     }
                  });
               }
            }
         }

         private ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults compile(float var1, float var2, float var3, ChunkBufferBuilderPack var4) {
            ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults var5 = new ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults();
            boolean var6 = true;
            BlockPos var7 = RenderChunk.this.origin.immutable();
            BlockPos var8 = var7.offset(15, 15, 15);
            VisGraph var9 = new VisGraph();
            RenderChunkRegion var10 = this.region;
            this.region = null;
            PoseStack var11 = new PoseStack();
            if (var10 != null) {
               ModelBlockRenderer.enableCaching();
               ReferenceArraySet var12 = new ReferenceArraySet(RenderType.chunkBufferLayers().size());
               RandomSource var13 = RandomSource.create();
               BlockRenderDispatcher var14 = Minecraft.getInstance().getBlockRenderer();

               for(BlockPos var16 : BlockPos.betweenClosed(var7, var8)) {
                  BlockState var17 = var10.getBlockState(var16);
                  if (var17.isSolidRender(var10, var16)) {
                     var9.setOpaque(var16);
                  }

                  if (var17.hasBlockEntity()) {
                     BlockEntity var18 = var10.getBlockEntity(var16);
                     if (var18 != null) {
                        this.handleBlockEntity(var5, var18);
                     }
                  }

                  BlockState var26 = var10.getBlockState(var16);
                  FluidState var19 = var26.getFluidState();
                  if (!var19.isEmpty()) {
                     RenderType var20 = ItemBlockRenderTypes.getRenderLayer(var19);
                     BufferBuilder var21 = var4.builder(var20);
                     if (var12.add(var20)) {
                        RenderChunk.this.beginLayer(var21);
                     }

                     var14.renderLiquid(var16, var10, var21, var26, var19);
                  }

                  if (var17.getRenderShape() != RenderShape.INVISIBLE) {
                     RenderType var27 = ItemBlockRenderTypes.getChunkRenderType(var17);
                     BufferBuilder var28 = var4.builder(var27);
                     if (var12.add(var27)) {
                        RenderChunk.this.beginLayer(var28);
                     }

                     var11.pushPose();
                     var11.translate((double)(var16.getX() & 15), (double)(var16.getY() & 15), (double)(var16.getZ() & 15));
                     var14.renderBatched(var17, var16, var10, var11, var28, true, var13);
                     var11.popPose();
                  }
               }

               if (var12.contains(RenderType.translucent())) {
                  BufferBuilder var22 = var4.builder(RenderType.translucent());
                  if (!var22.isCurrentBatchEmpty()) {
                     var22.setQuadSortOrigin(var1 - (float)var7.getX(), var2 - (float)var7.getY(), var3 - (float)var7.getZ());
                     var5.transparencyState = var22.getSortState();
                  }
               }

               for(RenderType var24 : var12) {
                  BufferBuilder.RenderedBuffer var25 = var4.builder(var24).endOrDiscardIfEmpty();
                  if (var25 != null) {
                     var5.renderedLayers.put(var24, var25);
                  }
               }

               ModelBlockRenderer.clearCache();
            }

            var5.visibilitySet = var9.resolve();
            return var5;
         }

         private <E extends BlockEntity> void handleBlockEntity(ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults var1, E var2) {
            BlockEntityRenderer var3 = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(var2);
            if (var3 != null) {
               var1.blockEntities.add(var2);
               if (var3.shouldRenderOffScreen(var2)) {
                  var1.globalBlockEntities.add(var2);
               }
            }
         }

         @Override
         public void cancel() {
            this.region = null;
            if (this.isCancelled.compareAndSet(false, true)) {
               RenderChunk.this.setDirty(false);
            }
         }

         static final class CompileResults {
            public final List<BlockEntity> globalBlockEntities = new ArrayList<>();
            public final List<BlockEntity> blockEntities = new ArrayList<>();
            public final Map<RenderType, BufferBuilder.RenderedBuffer> renderedLayers = new Reference2ObjectArrayMap();
            public VisibilitySet visibilitySet = new VisibilitySet();
            @Nullable
            public BufferBuilder.SortState transparencyState;

            CompileResults() {
               super();
            }
         }
      }

      class ResortTransparencyTask extends ChunkRenderDispatcher.RenderChunk.ChunkCompileTask {
         private final ChunkRenderDispatcher.CompiledChunk compiledChunk;

         public ResortTransparencyTask(double var2, ChunkRenderDispatcher.CompiledChunk var4) {
            super(var2, true);
            this.compiledChunk = var4;
         }

         @Override
         protected String name() {
            return "rend_chk_sort";
         }

         @Override
         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(ChunkBufferBuilderPack var1) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!RenderChunk.this.hasAllNeighbors()) {
               this.isCancelled.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vec3 var2 = ChunkRenderDispatcher.this.getCameraPosition();
               float var3 = (float)var2.x;
               float var4 = (float)var2.y;
               float var5 = (float)var2.z;
               BufferBuilder.SortState var6 = this.compiledChunk.transparencyState;
               if (var6 != null && !this.compiledChunk.isEmpty(RenderType.translucent())) {
                  BufferBuilder var7 = var1.builder(RenderType.translucent());
                  RenderChunk.this.beginLayer(var7);
                  var7.restoreSortState(var6);
                  var7.setQuadSortOrigin(
                     var3 - (float)RenderChunk.this.origin.getX(), var4 - (float)RenderChunk.this.origin.getY(), var5 - (float)RenderChunk.this.origin.getZ()
                  );
                  this.compiledChunk.transparencyState = var7.getSortState();
                  BufferBuilder.RenderedBuffer var8 = var7.end();
                  if (this.isCancelled.get()) {
                     var8.release();
                     return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                  } else {
                     CompletableFuture var9 = ChunkRenderDispatcher.this.uploadChunkLayer(var8, RenderChunk.this.getBuffer(RenderType.translucent()))
                        .thenApply(var0 -> ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                     return var9.handle((var1x, var2x) -> {
                        if (var2x != null && !(var2x instanceof CancellationException) && !(var2x instanceof InterruptedException)) {
                           Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var2x, "Rendering chunk"));
                        }

                        return this.isCancelled.get() ? ChunkRenderDispatcher.ChunkTaskResult.CANCELLED : ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     });
                  }
               } else {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               }
            }
         }

         @Override
         public void cancel() {
            this.isCancelled.set(true);
         }
      }
   }
}
