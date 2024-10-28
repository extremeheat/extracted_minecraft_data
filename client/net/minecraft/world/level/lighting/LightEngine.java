package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class LightEngine<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>> implements LayerLightEventListener {
   public static final int MAX_LEVEL = 15;
   protected static final int MIN_OPACITY = 1;
   protected static final long PULL_LIGHT_IN_ENTRY = LightEngine.QueueEntry.decreaseAllDirections(1);
   private static final int MIN_QUEUE_SIZE = 512;
   protected static final Direction[] PROPAGATION_DIRECTIONS = Direction.values();
   protected final LightChunkGetter chunkSource;
   protected final S storage;
   private final LongOpenHashSet blockNodesToCheck = new LongOpenHashSet(512, 0.5F);
   private final LongArrayFIFOQueue decreaseQueue = new LongArrayFIFOQueue();
   private final LongArrayFIFOQueue increaseQueue = new LongArrayFIFOQueue();
   private static final int CACHE_SIZE = 2;
   private final long[] lastChunkPos = new long[2];
   private final LightChunk[] lastChunk = new LightChunk[2];

   protected LightEngine(LightChunkGetter var1, S var2) {
      super();
      this.chunkSource = var1;
      this.storage = var2;
      this.clearChunkCache();
   }

   public static boolean hasDifferentLightProperties(BlockState var0, BlockState var1) {
      if (var1 == var0) {
         return false;
      } else {
         return var1.getLightBlock() != var0.getLightBlock() || var1.getLightEmission() != var0.getLightEmission() || var1.useShapeForLightOcclusion() || var0.useShapeForLightOcclusion();
      }
   }

   public static int getLightBlockInto(BlockState var0, BlockState var1, Direction var2, int var3) {
      boolean var4 = isEmptyShape(var0);
      boolean var5 = isEmptyShape(var1);
      if (var4 && var5) {
         return var3;
      } else {
         VoxelShape var6 = var4 ? Shapes.empty() : var0.getOcclusionShape();
         VoxelShape var7 = var5 ? Shapes.empty() : var1.getOcclusionShape();
         return Shapes.mergedFaceOccludes(var6, var7, var2) ? 16 : var3;
      }
   }

   public static VoxelShape getOcclusionShape(BlockState var0, Direction var1) {
      return isEmptyShape(var0) ? Shapes.empty() : var0.getFaceOcclusionShape(var1);
   }

   protected static boolean isEmptyShape(BlockState var0) {
      return !var0.canOcclude() || !var0.useShapeForLightOcclusion();
   }

   protected BlockState getState(BlockPos var1) {
      int var2 = SectionPos.blockToSectionCoord(var1.getX());
      int var3 = SectionPos.blockToSectionCoord(var1.getZ());
      LightChunk var4 = this.getChunk(var2, var3);
      return var4 == null ? Blocks.BEDROCK.defaultBlockState() : var4.getBlockState(var1);
   }

   protected int getOpacity(BlockState var1) {
      return Math.max(1, var1.getLightBlock());
   }

   protected boolean shapeOccludes(BlockState var1, BlockState var2, Direction var3) {
      VoxelShape var4 = getOcclusionShape(var1, var3);
      VoxelShape var5 = getOcclusionShape(var2, var3.getOpposite());
      return Shapes.faceShapeOccludes(var4, var5);
   }

   @Nullable
   protected LightChunk getChunk(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);

      for(int var5 = 0; var5 < 2; ++var5) {
         if (var3 == this.lastChunkPos[var5]) {
            return this.lastChunk[var5];
         }
      }

      LightChunk var7 = this.chunkSource.getChunkForLighting(var1, var2);

      for(int var6 = 1; var6 > 0; --var6) {
         this.lastChunkPos[var6] = this.lastChunkPos[var6 - 1];
         this.lastChunk[var6] = this.lastChunk[var6 - 1];
      }

      this.lastChunkPos[0] = var3;
      this.lastChunk[0] = var7;
      return var7;
   }

   private void clearChunkCache() {
      Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
      Arrays.fill(this.lastChunk, (Object)null);
   }

   public void checkBlock(BlockPos var1) {
      this.blockNodesToCheck.add(var1.asLong());
   }

   public void queueSectionData(long var1, @Nullable DataLayer var3) {
      this.storage.queueSectionData(var1, var3);
   }

   public void retainData(ChunkPos var1, boolean var2) {
      this.storage.retainData(SectionPos.getZeroNode(var1.x, var1.z), var2);
   }

   public void updateSectionStatus(SectionPos var1, boolean var2) {
      this.storage.updateSectionStatus(var1.asLong(), var2);
   }

   public void setLightEnabled(ChunkPos var1, boolean var2) {
      this.storage.setLightEnabled(SectionPos.getZeroNode(var1.x, var1.z), var2);
   }

   public int runLightUpdates() {
      LongIterator var1 = this.blockNodesToCheck.iterator();

      while(var1.hasNext()) {
         this.checkNode(var1.nextLong());
      }

      this.blockNodesToCheck.clear();
      this.blockNodesToCheck.trim(512);
      int var2 = 0;
      var2 += this.propagateDecreases();
      var2 += this.propagateIncreases();
      this.clearChunkCache();
      this.storage.markNewInconsistencies(this);
      this.storage.swapSectionMap();
      return var2;
   }

   private int propagateIncreases() {
      int var1;
      for(var1 = 0; !this.increaseQueue.isEmpty(); ++var1) {
         long var2 = this.increaseQueue.dequeueLong();
         long var4 = this.increaseQueue.dequeueLong();
         int var6 = this.storage.getStoredLevel(var2);
         int var7 = LightEngine.QueueEntry.getFromLevel(var4);
         if (LightEngine.QueueEntry.isIncreaseFromEmission(var4) && var6 < var7) {
            this.storage.setStoredLevel(var2, var7);
            var6 = var7;
         }

         if (var6 == var7) {
            this.propagateIncrease(var2, var4, var6);
         }
      }

      return var1;
   }

   private int propagateDecreases() {
      int var1;
      for(var1 = 0; !this.decreaseQueue.isEmpty(); ++var1) {
         long var2 = this.decreaseQueue.dequeueLong();
         long var4 = this.decreaseQueue.dequeueLong();
         this.propagateDecrease(var2, var4);
      }

      return var1;
   }

   protected void enqueueDecrease(long var1, long var3) {
      this.decreaseQueue.enqueue(var1);
      this.decreaseQueue.enqueue(var3);
   }

   protected void enqueueIncrease(long var1, long var3) {
      this.increaseQueue.enqueue(var1);
      this.increaseQueue.enqueue(var3);
   }

   public boolean hasLightWork() {
      return this.storage.hasInconsistencies() || !this.blockNodesToCheck.isEmpty() || !this.decreaseQueue.isEmpty() || !this.increaseQueue.isEmpty();
   }

   @Nullable
   public DataLayer getDataLayerData(SectionPos var1) {
      return this.storage.getDataLayerData(var1.asLong());
   }

   public int getLightValue(BlockPos var1) {
      return this.storage.getLightValue(var1.asLong());
   }

   public String getDebugData(long var1) {
      return this.getDebugSectionType(var1).display();
   }

   public LayerLightSectionStorage.SectionType getDebugSectionType(long var1) {
      return this.storage.getDebugSectionType(var1);
   }

   protected abstract void checkNode(long var1);

   protected abstract void propagateIncrease(long var1, long var3, int var5);

   protected abstract void propagateDecrease(long var1, long var3);

   public static class QueueEntry {
      private static final int FROM_LEVEL_BITS = 4;
      private static final int DIRECTION_BITS = 6;
      private static final long LEVEL_MASK = 15L;
      private static final long DIRECTIONS_MASK = 1008L;
      private static final long FLAG_FROM_EMPTY_SHAPE = 1024L;
      private static final long FLAG_INCREASE_FROM_EMISSION = 2048L;

      public QueueEntry() {
         super();
      }

      public static long decreaseSkipOneDirection(int var0, Direction var1) {
         long var2 = withoutDirection(1008L, var1);
         return withLevel(var2, var0);
      }

      public static long decreaseAllDirections(int var0) {
         return withLevel(1008L, var0);
      }

      public static long increaseLightFromEmission(int var0, boolean var1) {
         long var2 = 1008L;
         var2 |= 2048L;
         if (var1) {
            var2 |= 1024L;
         }

         return withLevel(var2, var0);
      }

      public static long increaseSkipOneDirection(int var0, boolean var1, Direction var2) {
         long var3 = withoutDirection(1008L, var2);
         if (var1) {
            var3 |= 1024L;
         }

         return withLevel(var3, var0);
      }

      public static long increaseOnlyOneDirection(int var0, boolean var1, Direction var2) {
         long var3 = 0L;
         if (var1) {
            var3 |= 1024L;
         }

         var3 = withDirection(var3, var2);
         return withLevel(var3, var0);
      }

      public static long increaseSkySourceInDirections(boolean var0, boolean var1, boolean var2, boolean var3, boolean var4) {
         long var5 = withLevel(0L, 15);
         if (var0) {
            var5 = withDirection(var5, Direction.DOWN);
         }

         if (var1) {
            var5 = withDirection(var5, Direction.NORTH);
         }

         if (var2) {
            var5 = withDirection(var5, Direction.SOUTH);
         }

         if (var3) {
            var5 = withDirection(var5, Direction.WEST);
         }

         if (var4) {
            var5 = withDirection(var5, Direction.EAST);
         }

         return var5;
      }

      public static int getFromLevel(long var0) {
         return (int)(var0 & 15L);
      }

      public static boolean isFromEmptyShape(long var0) {
         return (var0 & 1024L) != 0L;
      }

      public static boolean isIncreaseFromEmission(long var0) {
         return (var0 & 2048L) != 0L;
      }

      public static boolean shouldPropagateInDirection(long var0, Direction var2) {
         return (var0 & 1L << var2.ordinal() + 4) != 0L;
      }

      private static long withLevel(long var0, int var2) {
         return var0 & -16L | (long)var2 & 15L;
      }

      private static long withDirection(long var0, Direction var2) {
         return var0 | 1L << var2.ordinal() + 4;
      }

      private static long withoutDirection(long var0, Direction var2) {
         return var0 & ~(1L << var2.ordinal() + 4);
      }
   }
}
