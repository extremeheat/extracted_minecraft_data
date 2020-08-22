package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private final PriorityQueue toBatch = Queues.newPriorityQueue();
   private final Queue freeBuffers;
   private final Queue toUpload = Queues.newConcurrentLinkedQueue();
   private volatile int toBatchCount;
   private volatile int freeBufferCount;
   private final ChunkBufferBuilderPack fixedBuffers;
   private final ProcessorMailbox mailbox;
   private final Executor executor;
   private Level level;
   private final LevelRenderer renderer;
   private Vec3 camera;

   public ChunkRenderDispatcher(Level var1, LevelRenderer var2, Executor var3, boolean var4, ChunkBufferBuilderPack var5) {
      this.camera = Vec3.ZERO;
      this.level = var1;
      this.renderer = var2;
      int var6 = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / (RenderType.chunkBufferLayers().stream().mapToInt(RenderType::bufferSize).sum() * 4) - 1);
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

   public void setLevel(Level var1) {
      this.level = var1;
   }

   private void runTask() {
      if (!this.freeBuffers.isEmpty()) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var1 = (ChunkRenderDispatcher.RenderChunk.ChunkCompileTask)this.toBatch.poll();
         if (var1 != null) {
            ChunkBufferBuilderPack var2 = (ChunkBufferBuilderPack)this.freeBuffers.poll();
            this.toBatchCount = this.toBatch.size();
            this.freeBufferCount = this.freeBuffers.size();
            CompletableFuture.runAsync(() -> {
            }, this.executor).thenCompose((var2x) -> {
               return var1.doTask(var2);
            }).whenComplete((var2x, var3) -> {
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
               if (var3 != null) {
                  CrashReport var4 = CrashReport.forThrowable(var3, "Batching chunks");
                  Minecraft.getInstance().delayCrash(Minecraft.getInstance().fillReport(var4));
               }

            });
         }
      }
   }

   public String getStats() {
      return String.format("pC: %03d, pU: %02d, aB: %02d", this.toBatchCount, this.toUpload.size(), this.freeBufferCount);
   }

   public void setCamera(Vec3 var1) {
      this.camera = var1;
   }

   public Vec3 getCameraPosition() {
      return this.camera;
   }

   public boolean uploadAllPendingUploads() {
      boolean var1;
      Runnable var2;
      for(var1 = false; (var2 = (Runnable)this.toUpload.poll()) != null; var1 = true) {
         var2.run();
      }

      return var1;
   }

   public void rebuildChunkSync(ChunkRenderDispatcher.RenderChunk var1) {
      var1.compileSync();
   }

   public void blockUntilClear() {
      this.clearBatchQueue();
   }

   public void schedule(ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var1) {
      this.mailbox.tell(() -> {
         this.toBatch.offer(var1);
         this.toBatchCount = this.toBatch.size();
         this.runTask();
      });
   }

   public CompletableFuture uploadChunkLayer(BufferBuilder var1, VertexBuffer var2) {
      Runnable var10000 = () -> {
      };
      Queue var10001 = this.toUpload;
      var10001.getClass();
      return CompletableFuture.runAsync(var10000, var10001::add).thenCompose((var3) -> {
         return this.doUploadChunkLayer(var1, var2);
      });
   }

   private CompletableFuture doUploadChunkLayer(BufferBuilder var1, VertexBuffer var2) {
      return var2.uploadLater(var1);
   }

   private void clearBatchQueue() {
      while(!this.toBatch.isEmpty()) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var1 = (ChunkRenderDispatcher.RenderChunk.ChunkCompileTask)this.toBatch.poll();
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
      this.clearBatchQueue();
      this.mailbox.close();
      this.freeBuffers.clear();
   }

   public static class CompiledChunk {
      public static final ChunkRenderDispatcher.CompiledChunk UNCOMPILED = new ChunkRenderDispatcher.CompiledChunk() {
         public boolean facesCanSeeEachother(Direction var1, Direction var2) {
            return false;
         }
      };
      private final Set hasBlocks = new ObjectArraySet();
      private final Set hasLayer = new ObjectArraySet();
      private boolean isCompletelyEmpty = true;
      private final List renderableBlockEntities = Lists.newArrayList();
      private VisibilitySet visibilitySet = new VisibilitySet();
      @Nullable
      private BufferBuilder.State transparencyState;

      public boolean hasNoRenderableLayers() {
         return this.isCompletelyEmpty;
      }

      public boolean isEmpty(RenderType var1) {
         return !this.hasBlocks.contains(var1);
      }

      public List getRenderableBlockEntities() {
         return this.renderableBlockEntities;
      }

      public boolean facesCanSeeEachother(Direction var1, Direction var2) {
         return this.visibilitySet.visibilityBetween(var1, var2);
      }
   }

   static enum ChunkTaskResult {
      SUCCESSFUL,
      CANCELLED;
   }

   public class RenderChunk {
      public final AtomicReference compiled;
      @Nullable
      private ChunkRenderDispatcher.RenderChunk.RebuildTask lastRebuildTask;
      @Nullable
      private ChunkRenderDispatcher.RenderChunk.ResortTransparencyTask lastResortTransparencyTask;
      private final Set globalBlockEntities;
      private final Map buffers;
      public AABB bb;
      private int lastFrame;
      private boolean dirty;
      private final BlockPos.MutableBlockPos origin;
      private final BlockPos.MutableBlockPos[] relativeOrigins;
      private boolean playerChanged;

      public RenderChunk() {
         this.compiled = new AtomicReference(ChunkRenderDispatcher.CompiledChunk.UNCOMPILED);
         this.globalBlockEntities = Sets.newHashSet();
         this.buffers = (Map)RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((var0) -> {
            return var0;
         }, (var0) -> {
            return new VertexBuffer(DefaultVertexFormat.BLOCK);
         }));
         this.lastFrame = -1;
         this.dirty = true;
         this.origin = new BlockPos.MutableBlockPos(-1, -1, -1);
         this.relativeOrigins = (BlockPos.MutableBlockPos[])Util.make(new BlockPos.MutableBlockPos[6], (var0) -> {
            for(int var1 = 0; var1 < var0.length; ++var1) {
               var0[var1] = new BlockPos.MutableBlockPos();
            }

         });
      }

      private boolean doesChunkExistAt(BlockPos var1) {
         return ChunkRenderDispatcher.this.level.getChunk(var1.getX() >> 4, var1.getZ() >> 4, ChunkStatus.FULL, false) != null;
      }

      public boolean hasAllNeighbors() {
         boolean var1 = true;
         if (this.getDistToPlayerSqr() <= 576.0D) {
            return true;
         } else {
            return this.doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()]);
         }
      }

      public boolean setFrame(int var1) {
         if (this.lastFrame == var1) {
            return false;
         } else {
            this.lastFrame = var1;
            return true;
         }
      }

      public VertexBuffer getBuffer(RenderType var1) {
         return (VertexBuffer)this.buffers.get(var1);
      }

      public void setOrigin(int var1, int var2, int var3) {
         if (var1 != this.origin.getX() || var2 != this.origin.getY() || var3 != this.origin.getZ()) {
            this.reset();
            this.origin.set(var1, var2, var3);
            this.bb = new AABB((double)var1, (double)var2, (double)var3, (double)(var1 + 16), (double)(var2 + 16), (double)(var3 + 16));
            Direction[] var4 = Direction.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Direction var7 = var4[var6];
               this.relativeOrigins[var7.ordinal()].set((Vec3i)this.origin).move(var7, 16);
            }

         }
      }

      protected double getDistToPlayerSqr() {
         Camera var1 = Minecraft.getInstance().gameRenderer.getMainCamera();
         double var2 = this.bb.minX + 8.0D - var1.getPosition().x;
         double var4 = this.bb.minY + 8.0D - var1.getPosition().y;
         double var6 = this.bb.minZ + 8.0D - var1.getPosition().z;
         return var2 * var2 + var4 * var4 + var6 * var6;
      }

      private void beginLayer(BufferBuilder var1) {
         var1.begin(7, DefaultVertexFormat.BLOCK);
      }

      public ChunkRenderDispatcher.CompiledChunk getCompiledChunk() {
         return (ChunkRenderDispatcher.CompiledChunk)this.compiled.get();
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

         if (!var3.hasLayer.contains(var1)) {
            return false;
         } else {
            this.lastResortTransparencyTask = new ChunkRenderDispatcher.RenderChunk.ResortTransparencyTask(this.getDistToPlayerSqr(), var3);
            var2.schedule(this.lastResortTransparencyTask);
            return true;
         }
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

      public ChunkRenderDispatcher.RenderChunk.ChunkCompileTask createCompileTask() {
         this.cancelTasks();
         BlockPos var1 = this.origin.immutable();
         boolean var2 = true;
         RenderChunkRegion var3 = RenderChunkRegion.createIfNotEmpty(ChunkRenderDispatcher.this.level, var1.offset(-1, -1, -1), var1.offset(16, 16, 16), 1);
         this.lastRebuildTask = new ChunkRenderDispatcher.RenderChunk.RebuildTask(this.getDistToPlayerSqr(), var3);
         return this.lastRebuildTask;
      }

      public void rebuildChunkAsync(ChunkRenderDispatcher var1) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var2 = this.createCompileTask();
         var1.schedule(var2);
      }

      private void updateGlobalBlockEntities(Set var1) {
         HashSet var2 = Sets.newHashSet(var1);
         HashSet var3 = Sets.newHashSet(this.globalBlockEntities);
         var2.removeAll(this.globalBlockEntities);
         var3.removeAll(var1);
         this.globalBlockEntities.clear();
         this.globalBlockEntities.addAll(var1);
         ChunkRenderDispatcher.this.renderer.updateGlobalBlockEntities(var3, var2);
      }

      public void compileSync() {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var1 = this.createCompileTask();
         var1.doTask(ChunkRenderDispatcher.this.fixedBuffers);
      }

      abstract class ChunkCompileTask implements Comparable {
         protected final double distAtCreation;
         protected final AtomicBoolean isCancelled = new AtomicBoolean(false);

         public ChunkCompileTask(double var2) {
            this.distAtCreation = var2;
         }

         public abstract CompletableFuture doTask(ChunkBufferBuilderPack var1);

         public abstract void cancel();

         public int compareTo(ChunkRenderDispatcher.RenderChunk.ChunkCompileTask var1) {
            return Doubles.compare(this.distAtCreation, var1.distAtCreation);
         }

         // $FF: synthetic method
         public int compareTo(Object var1) {
            return this.compareTo((ChunkRenderDispatcher.RenderChunk.ChunkCompileTask)var1);
         }
      }

      class ResortTransparencyTask extends ChunkRenderDispatcher.RenderChunk.ChunkCompileTask {
         private final ChunkRenderDispatcher.CompiledChunk compiledChunk;

         public ResortTransparencyTask(double var2, ChunkRenderDispatcher.CompiledChunk var4) {
            super(var2);
            this.compiledChunk = var4;
         }

         public CompletableFuture doTask(ChunkBufferBuilderPack var1) {
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
               BufferBuilder.State var6 = this.compiledChunk.transparencyState;
               if (var6 != null && this.compiledChunk.hasBlocks.contains(RenderType.translucent())) {
                  BufferBuilder var7 = var1.builder(RenderType.translucent());
                  RenderChunk.this.beginLayer(var7);
                  var7.restoreState(var6);
                  var7.sortQuads(var3 - (float)RenderChunk.this.origin.getX(), var4 - (float)RenderChunk.this.origin.getY(), var5 - (float)RenderChunk.this.origin.getZ());
                  this.compiledChunk.transparencyState = var7.getState();
                  var7.end();
                  if (this.isCancelled.get()) {
                     return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                  } else {
                     CompletableFuture var8 = ChunkRenderDispatcher.this.uploadChunkLayer(var1.builder(RenderType.translucent()), RenderChunk.this.getBuffer(RenderType.translucent())).thenApply((var0) -> {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     });
                     return var8.handle((var1x, var2x) -> {
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

         public void cancel() {
            this.isCancelled.set(true);
         }
      }

      class RebuildTask extends ChunkRenderDispatcher.RenderChunk.ChunkCompileTask {
         @Nullable
         protected RenderChunkRegion region;

         public RebuildTask(double var2, RenderChunkRegion var4) {
            super(var2);
            this.region = var4;
         }

         public CompletableFuture doTask(ChunkBufferBuilderPack var1) {
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
               ChunkRenderDispatcher.CompiledChunk var6 = new ChunkRenderDispatcher.CompiledChunk();
               Set var7 = this.compile(var3, var4, var5, var6, var1);
               RenderChunk.this.updateGlobalBlockEntities(var7);
               if (this.isCancelled.get()) {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               } else {
                  ArrayList var8 = Lists.newArrayList();
                  var6.hasLayer.forEach((var3x) -> {
                     var8.add(ChunkRenderDispatcher.this.uploadChunkLayer(var1.builder(var3x), RenderChunk.this.getBuffer(var3x)));
                  });
                  return Util.sequence(var8).handle((var2x, var3x) -> {
                     if (var3x != null && !(var3x instanceof CancellationException) && !(var3x instanceof InterruptedException)) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var3x, "Rendering chunk"));
                     }

                     if (this.isCancelled.get()) {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     } else {
                        RenderChunk.this.compiled.set(var6);
                        return ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     }
                  });
               }
            }
         }

         private Set compile(float var1, float var2, float var3, ChunkRenderDispatcher.CompiledChunk var4, ChunkBufferBuilderPack var5) {
            boolean var6 = true;
            BlockPos var7 = RenderChunk.this.origin.immutable();
            BlockPos var8 = var7.offset(15, 15, 15);
            VisGraph var9 = new VisGraph();
            HashSet var10 = Sets.newHashSet();
            RenderChunkRegion var11 = this.region;
            this.region = null;
            PoseStack var12 = new PoseStack();
            if (var11 != null) {
               ModelBlockRenderer.enableCaching();
               Random var13 = new Random();
               BlockRenderDispatcher var14 = Minecraft.getInstance().getBlockRenderer();
               Iterator var15 = BlockPos.betweenClosed(var7, var8).iterator();

               while(var15.hasNext()) {
                  BlockPos var16 = (BlockPos)var15.next();
                  BlockState var17 = var11.getBlockState(var16);
                  Block var18 = var17.getBlock();
                  if (var17.isSolidRender(var11, var16)) {
                     var9.setOpaque(var16);
                  }

                  if (var18.isEntityBlock()) {
                     BlockEntity var19 = var11.getBlockEntity(var16, LevelChunk.EntityCreationType.CHECK);
                     if (var19 != null) {
                        this.handleBlockEntity(var4, var10, var19);
                     }
                  }

                  FluidState var23 = var11.getFluidState(var16);
                  RenderType var20;
                  BufferBuilder var21;
                  if (!var23.isEmpty()) {
                     var20 = ItemBlockRenderTypes.getRenderLayer(var23);
                     var21 = var5.builder(var20);
                     if (var4.hasLayer.add(var20)) {
                        RenderChunk.this.beginLayer(var21);
                     }

                     if (var14.renderLiquid(var16, var11, var21, var23)) {
                        var4.isCompletelyEmpty = false;
                        var4.hasBlocks.add(var20);
                     }
                  }

                  if (var17.getRenderShape() != RenderShape.INVISIBLE) {
                     var20 = ItemBlockRenderTypes.getChunkRenderType(var17);
                     var21 = var5.builder(var20);
                     if (var4.hasLayer.add(var20)) {
                        RenderChunk.this.beginLayer(var21);
                     }

                     var12.pushPose();
                     var12.translate((double)(var16.getX() & 15), (double)(var16.getY() & 15), (double)(var16.getZ() & 15));
                     if (var14.renderBatched(var17, var16, var11, var12, var21, true, var13)) {
                        var4.isCompletelyEmpty = false;
                        var4.hasBlocks.add(var20);
                     }

                     var12.popPose();
                  }
               }

               if (var4.hasBlocks.contains(RenderType.translucent())) {
                  BufferBuilder var22 = var5.builder(RenderType.translucent());
                  var22.sortQuads(var1 - (float)var7.getX(), var2 - (float)var7.getY(), var3 - (float)var7.getZ());
                  var4.transparencyState = var22.getState();
               }

               Stream var10000 = var4.hasLayer.stream();
               var5.getClass();
               var10000.map(var5::builder).forEach(BufferBuilder::end);
               ModelBlockRenderer.clearCache();
            }

            var4.visibilitySet = var9.resolve();
            return var10;
         }

         private void handleBlockEntity(ChunkRenderDispatcher.CompiledChunk var1, Set var2, BlockEntity var3) {
            BlockEntityRenderer var4 = BlockEntityRenderDispatcher.instance.getRenderer(var3);
            if (var4 != null) {
               var1.renderableBlockEntities.add(var3);
               if (var4.shouldRenderOffScreen(var3)) {
                  var2.add(var3);
               }
            }

         }

         public void cancel() {
            this.region = null;
            if (this.isCancelled.compareAndSet(false, true)) {
               RenderChunk.this.setDirty(false);
            }

         }
      }
   }
}
