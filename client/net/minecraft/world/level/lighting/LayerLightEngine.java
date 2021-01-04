package net.minecraft.world.level.lighting;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class LayerLightEngine<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>> extends DynamicGraphMinFixedPoint implements LayerLightEventListener {
   private static final Direction[] DIRECTIONS = Direction.values();
   protected final LightChunkGetter chunkSource;
   protected final LightLayer layer;
   protected final S storage;
   private boolean runningLightUpdates;
   protected final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
   private final long[] lastChunkPos = new long[2];
   private final BlockGetter[] lastChunk = new BlockGetter[2];

   public LayerLightEngine(LightChunkGetter var1, LightLayer var2, S var3) {
      super(16, 256, 8192);
      this.chunkSource = var1;
      this.layer = var2;
      this.storage = var3;
      this.clearCache();
   }

   protected void checkNode(long var1) {
      this.storage.runAllUpdates();
      if (this.storage.storingLightForSection(SectionPos.blockToSection(var1))) {
         super.checkNode(var1);
      }

   }

   @Nullable
   private BlockGetter getChunk(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);

      for(int var5 = 0; var5 < 2; ++var5) {
         if (var3 == this.lastChunkPos[var5]) {
            return this.lastChunk[var5];
         }
      }

      BlockGetter var7 = this.chunkSource.getChunkForLighting(var1, var2);

      for(int var6 = 1; var6 > 0; --var6) {
         this.lastChunkPos[var6] = this.lastChunkPos[var6 - 1];
         this.lastChunk[var6] = this.lastChunk[var6 - 1];
      }

      this.lastChunkPos[0] = var3;
      this.lastChunk[0] = var7;
      return var7;
   }

   private void clearCache() {
      Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
      Arrays.fill(this.lastChunk, (Object)null);
   }

   protected BlockState getStateAndOpacity(long var1, @Nullable AtomicInteger var3) {
      if (var1 == 9223372036854775807L) {
         if (var3 != null) {
            var3.set(0);
         }

         return Blocks.AIR.defaultBlockState();
      } else {
         int var4 = SectionPos.blockToSectionCoord(BlockPos.getX(var1));
         int var5 = SectionPos.blockToSectionCoord(BlockPos.getZ(var1));
         BlockGetter var6 = this.getChunk(var4, var5);
         if (var6 == null) {
            if (var3 != null) {
               var3.set(16);
            }

            return Blocks.BEDROCK.defaultBlockState();
         } else {
            this.pos.set(var1);
            BlockState var7 = var6.getBlockState(this.pos);
            boolean var8 = var7.canOcclude() && var7.useShapeForLightOcclusion();
            if (var3 != null) {
               var3.set(var7.getLightBlock(this.chunkSource.getLevel(), this.pos));
            }

            return var8 ? var7 : Blocks.AIR.defaultBlockState();
         }
      }
   }

   protected VoxelShape getShape(BlockState var1, long var2, Direction var4) {
      return var1.canOcclude() ? var1.getFaceOcclusionShape(this.chunkSource.getLevel(), this.pos.set(var2), var4) : Shapes.empty();
   }

   public static int getLightBlockInto(BlockGetter var0, BlockState var1, BlockPos var2, BlockState var3, BlockPos var4, Direction var5, int var6) {
      boolean var7 = var1.canOcclude() && var1.useShapeForLightOcclusion();
      boolean var8 = var3.canOcclude() && var3.useShapeForLightOcclusion();
      if (!var7 && !var8) {
         return var6;
      } else {
         VoxelShape var9 = var7 ? var1.getOcclusionShape(var0, var2) : Shapes.empty();
         VoxelShape var10 = var8 ? var3.getOcclusionShape(var0, var4) : Shapes.empty();
         return Shapes.mergedFaceOccludes(var9, var10, var5) ? 16 : var6;
      }
   }

   protected boolean isSource(long var1) {
      return var1 == 9223372036854775807L;
   }

   protected int getComputedLevel(long var1, long var3, int var5) {
      return 0;
   }

   protected int getLevel(long var1) {
      return var1 == 9223372036854775807L ? 0 : 15 - this.storage.getStoredLevel(var1);
   }

   protected int getLevel(DataLayer var1, long var2) {
      return 15 - var1.get(SectionPos.sectionRelative(BlockPos.getX(var2)), SectionPos.sectionRelative(BlockPos.getY(var2)), SectionPos.sectionRelative(BlockPos.getZ(var2)));
   }

   protected void setLevel(long var1, int var3) {
      this.storage.setStoredLevel(var1, Math.min(15, 15 - var3));
   }

   protected int computeLevelFromNeighbor(long var1, long var3, int var5) {
      return 0;
   }

   public boolean hasLightWork() {
      return this.hasWork() || this.storage.hasWork() || this.storage.hasInconsistencies();
   }

   public int runUpdates(int var1, boolean var2, boolean var3) {
      if (!this.runningLightUpdates) {
         if (this.storage.hasWork()) {
            var1 = this.storage.runUpdates(var1);
            if (var1 == 0) {
               return var1;
            }
         }

         this.storage.markNewInconsistencies(this, var2, var3);
      }

      this.runningLightUpdates = true;
      if (this.hasWork()) {
         var1 = this.runUpdates(var1);
         this.clearCache();
         if (var1 == 0) {
            return var1;
         }
      }

      this.runningLightUpdates = false;
      this.storage.swapSectionMap();
      return var1;
   }

   protected void queueSectionData(long var1, @Nullable DataLayer var3) {
      this.storage.queueSectionData(var1, var3);
   }

   @Nullable
   public DataLayer getDataLayerData(SectionPos var1) {
      return this.storage.getDataLayerData(var1.asLong());
   }

   public int getLightValue(BlockPos var1) {
      return this.storage.getLightValue(var1.asLong());
   }

   public String getDebugData(long var1) {
      return "" + this.storage.getLevel(var1);
   }

   public void checkBlock(BlockPos var1) {
      long var2 = var1.asLong();
      this.checkNode(var2);
      Direction[] var4 = DIRECTIONS;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         this.checkNode(BlockPos.offset(var2, var7));
      }

   }

   public void onBlockEmissionIncrease(BlockPos var1, int var2) {
   }

   public void updateSectionStatus(SectionPos var1, boolean var2) {
      this.storage.updateSectionStatus(var1.asLong(), var2);
   }

   public void enableLightSources(ChunkPos var1, boolean var2) {
      long var3 = SectionPos.getZeroNode(SectionPos.asLong(var1.x, 0, var1.z));
      this.storage.runAllUpdates();
      this.storage.enableLightSources(var3, var2);
   }

   public void retainData(ChunkPos var1, boolean var2) {
      long var3 = SectionPos.getZeroNode(SectionPos.asLong(var1.x, 0, var1.z));
      this.storage.retainData(var3, var2);
   }
}
