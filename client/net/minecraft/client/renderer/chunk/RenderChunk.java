package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

public class RenderChunk {
   private volatile Level level;
   private final LevelRenderer renderer;
   public static int updateCounter;
   public CompiledChunk compiled;
   private final ReentrantLock taskLock;
   private final ReentrantLock compileLock;
   private ChunkCompileTask pendingTask;
   private final Set<BlockEntity> globalBlockEntities;
   private final VertexBuffer[] buffers;
   public AABB bb;
   private int lastFrame;
   private boolean dirty;
   private final BlockPos.MutableBlockPos origin;
   private final BlockPos.MutableBlockPos[] relativeOrigins;
   private boolean playerChanged;

   public RenderChunk(Level var1, LevelRenderer var2) {
      super();
      this.compiled = CompiledChunk.UNCOMPILED;
      this.taskLock = new ReentrantLock();
      this.compileLock = new ReentrantLock();
      this.globalBlockEntities = Sets.newHashSet();
      this.buffers = new VertexBuffer[BlockLayer.values().length];
      this.lastFrame = -1;
      this.dirty = true;
      this.origin = new BlockPos.MutableBlockPos(-1, -1, -1);
      this.relativeOrigins = (BlockPos.MutableBlockPos[])Util.make(new BlockPos.MutableBlockPos[6], (var0) -> {
         for(int var1 = 0; var1 < var0.length; ++var1) {
            var0[var1] = new BlockPos.MutableBlockPos();
         }

      });
      this.level = var1;
      this.renderer = var2;
      if (GLX.useVbo()) {
         for(int var3 = 0; var3 < BlockLayer.values().length; ++var3) {
            this.buffers[var3] = new VertexBuffer(DefaultVertexFormat.BLOCK);
         }
      }

   }

   private static boolean doesChunkExistAt(BlockPos var0, Level var1) {
      return !var1.getChunk(var0.getX() >> 4, var0.getZ() >> 4).isEmpty();
   }

   public boolean hasAllNeighbors() {
      boolean var1 = true;
      if (this.getDistToPlayerSqr() <= 576.0D) {
         return true;
      } else {
         Level var2 = this.getLevel();
         return doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()], var2) && doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()], var2) && doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()], var2) && doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()], var2);
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

   public VertexBuffer getBuffer(int var1) {
      return this.buffers[var1];
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

   public void rebuildTransparent(float var1, float var2, float var3, ChunkCompileTask var4) {
      CompiledChunk var5 = var4.getCompiledChunk();
      if (var5.getTransparencyState() != null && !var5.isEmpty(BlockLayer.TRANSLUCENT)) {
         this.beginLayer(var4.getBuilders().builder(BlockLayer.TRANSLUCENT), this.origin);
         var4.getBuilders().builder(BlockLayer.TRANSLUCENT).restoreState(var5.getTransparencyState());
         this.preEndLayer(BlockLayer.TRANSLUCENT, var1, var2, var3, var4.getBuilders().builder(BlockLayer.TRANSLUCENT), var5);
      }
   }

   public void compile(float var1, float var2, float var3, ChunkCompileTask var4) {
      CompiledChunk var5 = new CompiledChunk();
      boolean var6 = true;
      BlockPos var7 = this.origin.immutable();
      BlockPos var8 = var7.offset(15, 15, 15);
      Level var9 = this.level;
      if (var9 != null) {
         var4.getStatusLock().lock();

         try {
            if (var4.getStatus() != ChunkCompileTask.Status.COMPILING) {
               return;
            }

            var4.setCompiledChunk(var5);
         } finally {
            var4.getStatusLock().unlock();
         }

         VisGraph var10 = new VisGraph();
         HashSet var11 = Sets.newHashSet();
         RenderChunkRegion var12 = var4.takeRegion();
         if (var12 != null) {
            ++updateCounter;
            boolean[] var13 = new boolean[BlockLayer.values().length];
            ModelBlockRenderer.enableCaching();
            Random var14 = new Random();
            BlockRenderDispatcher var15 = Minecraft.getInstance().getBlockRenderer();
            Iterator var16 = BlockPos.betweenClosed(var7, var8).iterator();

            while(var16.hasNext()) {
               BlockPos var17 = (BlockPos)var16.next();
               BlockState var18 = var12.getBlockState(var17);
               Block var19 = var18.getBlock();
               if (var18.isSolidRender(var12, var17)) {
                  var10.setOpaque(var17);
               }

               if (var19.isEntityBlock()) {
                  BlockEntity var20 = var12.getBlockEntity(var17, LevelChunk.EntityCreationType.CHECK);
                  if (var20 != null) {
                     BlockEntityRenderer var21 = BlockEntityRenderDispatcher.instance.getRenderer(var20);
                     if (var21 != null) {
                        var5.addRenderableBlockEntity(var20);
                        if (var21.shouldRenderOffScreen(var20)) {
                           var11.add(var20);
                        }
                     }
                  }
               }

               FluidState var37 = var12.getFluidState(var17);
               int var22;
               BufferBuilder var23;
               BlockLayer var38;
               if (!var37.isEmpty()) {
                  var38 = var37.getRenderLayer();
                  var22 = var38.ordinal();
                  var23 = var4.getBuilders().builder(var22);
                  if (!var5.hasLayer(var38)) {
                     var5.layerIsPresent(var38);
                     this.beginLayer(var23, var7);
                  }

                  var13[var22] |= var15.renderLiquid(var17, var12, var23, var37);
               }

               if (var18.getRenderShape() != RenderShape.INVISIBLE) {
                  var38 = var19.getRenderLayer();
                  var22 = var38.ordinal();
                  var23 = var4.getBuilders().builder(var22);
                  if (!var5.hasLayer(var38)) {
                     var5.layerIsPresent(var38);
                     this.beginLayer(var23, var7);
                  }

                  var13[var22] |= var15.renderBatched(var18, var17, var12, var23, var14);
               }
            }

            BlockLayer[] var33 = BlockLayer.values();
            int var34 = var33.length;

            for(int var35 = 0; var35 < var34; ++var35) {
               BlockLayer var36 = var33[var35];
               if (var13[var36.ordinal()]) {
                  var5.setChanged(var36);
               }

               if (var5.hasLayer(var36)) {
                  this.preEndLayer(var36, var1, var2, var3, var4.getBuilders().builder(var36), var5);
               }
            }

            ModelBlockRenderer.clearCache();
         }

         var5.setVisibilitySet(var10.resolve());
         this.taskLock.lock();

         try {
            HashSet var31 = Sets.newHashSet(var11);
            HashSet var32 = Sets.newHashSet(this.globalBlockEntities);
            var31.removeAll(this.globalBlockEntities);
            var32.removeAll(var11);
            this.globalBlockEntities.clear();
            this.globalBlockEntities.addAll(var11);
            this.renderer.updateGlobalBlockEntities(var32, var31);
         } finally {
            this.taskLock.unlock();
         }

      }
   }

   protected void cancelCompile() {
      this.taskLock.lock();

      try {
         if (this.pendingTask != null && this.pendingTask.getStatus() != ChunkCompileTask.Status.DONE) {
            this.pendingTask.cancel();
            this.pendingTask = null;
         }
      } finally {
         this.taskLock.unlock();
      }

   }

   public ReentrantLock getTaskLock() {
      return this.taskLock;
   }

   public ChunkCompileTask createCompileTask() {
      this.taskLock.lock();

      ChunkCompileTask var4;
      try {
         this.cancelCompile();
         BlockPos var1 = this.origin.immutable();
         boolean var2 = true;
         RenderChunkRegion var3 = RenderChunkRegion.createIfNotEmpty(this.level, var1.offset(-1, -1, -1), var1.offset(16, 16, 16), 1);
         this.pendingTask = new ChunkCompileTask(this, ChunkCompileTask.Type.REBUILD_CHUNK, this.getDistToPlayerSqr(), var3);
         var4 = this.pendingTask;
      } finally {
         this.taskLock.unlock();
      }

      return var4;
   }

   @Nullable
   public ChunkCompileTask createTransparencySortTask() {
      this.taskLock.lock();

      ChunkCompileTask var1;
      try {
         if (this.pendingTask == null || this.pendingTask.getStatus() != ChunkCompileTask.Status.PENDING) {
            if (this.pendingTask != null && this.pendingTask.getStatus() != ChunkCompileTask.Status.DONE) {
               this.pendingTask.cancel();
               this.pendingTask = null;
            }

            this.pendingTask = new ChunkCompileTask(this, ChunkCompileTask.Type.RESORT_TRANSPARENCY, this.getDistToPlayerSqr(), (RenderChunkRegion)null);
            this.pendingTask.setCompiledChunk(this.compiled);
            var1 = this.pendingTask;
            return var1;
         }

         var1 = null;
      } finally {
         this.taskLock.unlock();
      }

      return var1;
   }

   protected double getDistToPlayerSqr() {
      Camera var1 = Minecraft.getInstance().gameRenderer.getMainCamera();
      double var2 = this.bb.minX + 8.0D - var1.getPosition().x;
      double var4 = this.bb.minY + 8.0D - var1.getPosition().y;
      double var6 = this.bb.minZ + 8.0D - var1.getPosition().z;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   private void beginLayer(BufferBuilder var1, BlockPos var2) {
      var1.begin(7, DefaultVertexFormat.BLOCK);
      var1.offset((double)(-var2.getX()), (double)(-var2.getY()), (double)(-var2.getZ()));
   }

   private void preEndLayer(BlockLayer var1, float var2, float var3, float var4, BufferBuilder var5, CompiledChunk var6) {
      if (var1 == BlockLayer.TRANSLUCENT && !var6.isEmpty(var1)) {
         var5.sortQuads(var2, var3, var4);
         var6.setTransparencyState(var5.getState());
      }

      var5.end();
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiled;
   }

   public void setCompiledChunk(CompiledChunk var1) {
      this.compileLock.lock();

      try {
         this.compiled = var1;
      } finally {
         this.compileLock.unlock();
      }

   }

   public void reset() {
      this.cancelCompile();
      this.compiled = CompiledChunk.UNCOMPILED;
      this.dirty = true;
   }

   public void releaseBuffers() {
      this.reset();
      this.level = null;

      for(int var1 = 0; var1 < BlockLayer.values().length; ++var1) {
         if (this.buffers[var1] != null) {
            this.buffers[var1].delete();
         }
      }

   }

   public BlockPos getOrigin() {
      return this.origin;
   }

   public void setDirty(boolean var1) {
      if (this.dirty) {
         var1 |= this.playerChanged;
      }

      this.dirty = true;
      this.playerChanged = var1;
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

   public Level getLevel() {
      return this.level;
   }
}
