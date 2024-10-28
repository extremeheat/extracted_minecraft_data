package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.BitStorage;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChunkSkyLightSources {
   private static final int SIZE = 16;
   public static final int NEGATIVE_INFINITY = -2147483648;
   private final int minY;
   private final BitStorage heightmap;
   private final BlockPos.MutableBlockPos mutablePos1 = new BlockPos.MutableBlockPos();
   private final BlockPos.MutableBlockPos mutablePos2 = new BlockPos.MutableBlockPos();

   public ChunkSkyLightSources(LevelHeightAccessor var1) {
      super();
      this.minY = var1.getMinBuildHeight() - 1;
      int var2 = var1.getMaxBuildHeight();
      int var3 = Mth.ceillog2(var2 - this.minY + 1);
      this.heightmap = new SimpleBitStorage(var3, 256);
   }

   public void fillFrom(ChunkAccess var1) {
      int var2 = var1.getHighestFilledSectionIndex();
      if (var2 == -1) {
         this.fill(this.minY);
      } else {
         for(int var3 = 0; var3 < 16; ++var3) {
            for(int var4 = 0; var4 < 16; ++var4) {
               int var5 = Math.max(this.findLowestSourceY(var1, var2, var4, var3), this.minY);
               this.set(index(var4, var3), var5);
            }
         }

      }
   }

   private int findLowestSourceY(ChunkAccess var1, int var2, int var3, int var4) {
      int var5 = SectionPos.sectionToBlockCoord(var1.getSectionYFromSectionIndex(var2) + 1);
      BlockPos.MutableBlockPos var6 = this.mutablePos1.set(var3, var5, var4);
      BlockPos.MutableBlockPos var7 = this.mutablePos2.setWithOffset(var6, (Direction)Direction.DOWN);
      BlockState var8 = Blocks.AIR.defaultBlockState();

      for(int var9 = var2; var9 >= 0; --var9) {
         LevelChunkSection var10 = var1.getSection(var9);
         int var11;
         if (var10.hasOnlyAir()) {
            var8 = Blocks.AIR.defaultBlockState();
            var11 = var1.getSectionYFromSectionIndex(var9);
            var6.setY(SectionPos.sectionToBlockCoord(var11));
            var7.setY(var6.getY() - 1);
         } else {
            for(var11 = 15; var11 >= 0; --var11) {
               BlockState var12 = var10.getBlockState(var3, var11, var4);
               if (isEdgeOccluded(var1, var6, var8, var7, var12)) {
                  return var6.getY();
               }

               var8 = var12;
               var6.set(var7);
               var7.move(Direction.DOWN);
            }
         }
      }

      return this.minY;
   }

   public boolean update(BlockGetter var1, int var2, int var3, int var4) {
      int var5 = var3 + 1;
      int var6 = index(var2, var4);
      int var7 = this.get(var6);
      if (var5 < var7) {
         return false;
      } else {
         BlockPos.MutableBlockPos var8 = this.mutablePos1.set(var2, var3 + 1, var4);
         BlockState var9 = var1.getBlockState(var8);
         BlockPos.MutableBlockPos var10 = this.mutablePos2.set(var2, var3, var4);
         BlockState var11 = var1.getBlockState(var10);
         if (this.updateEdge(var1, var6, var7, var8, var9, var10, var11)) {
            return true;
         } else {
            BlockPos.MutableBlockPos var12 = this.mutablePos1.set(var2, var3 - 1, var4);
            BlockState var13 = var1.getBlockState(var12);
            return this.updateEdge(var1, var6, var7, var10, var11, var12, var13);
         }
      }
   }

   private boolean updateEdge(BlockGetter var1, int var2, int var3, BlockPos var4, BlockState var5, BlockPos var6, BlockState var7) {
      int var8 = var4.getY();
      if (isEdgeOccluded(var1, var4, var5, var6, var7)) {
         if (var8 > var3) {
            this.set(var2, var8);
            return true;
         }
      } else if (var8 == var3) {
         this.set(var2, this.findLowestSourceBelow(var1, var6, var7));
         return true;
      }

      return false;
   }

   private int findLowestSourceBelow(BlockGetter var1, BlockPos var2, BlockState var3) {
      BlockPos.MutableBlockPos var4 = this.mutablePos1.set(var2);
      BlockPos.MutableBlockPos var5 = this.mutablePos2.setWithOffset(var2, (Direction)Direction.DOWN);
      BlockState var6 = var3;

      while(var5.getY() >= this.minY) {
         BlockState var7 = var1.getBlockState(var5);
         if (isEdgeOccluded(var1, var4, var6, var5, var7)) {
            return var4.getY();
         }

         var6 = var7;
         var4.set(var5);
         var5.move(Direction.DOWN);
      }

      return this.minY;
   }

   private static boolean isEdgeOccluded(BlockGetter var0, BlockPos var1, BlockState var2, BlockPos var3, BlockState var4) {
      if (var4.getLightBlock(var0, var3) != 0) {
         return true;
      } else {
         VoxelShape var5 = LightEngine.getOcclusionShape(var0, var1, var2, Direction.DOWN);
         VoxelShape var6 = LightEngine.getOcclusionShape(var0, var3, var4, Direction.UP);
         return Shapes.faceShapeOccludes(var5, var6);
      }
   }

   public int getLowestSourceY(int var1, int var2) {
      int var3 = this.get(index(var1, var2));
      return this.extendSourcesBelowWorld(var3);
   }

   public int getHighestLowestSourceY() {
      int var1 = -2147483648;

      for(int var2 = 0; var2 < this.heightmap.getSize(); ++var2) {
         int var3 = this.heightmap.get(var2);
         if (var3 > var1) {
            var1 = var3;
         }
      }

      return this.extendSourcesBelowWorld(var1 + this.minY);
   }

   private void fill(int var1) {
      int var2 = var1 - this.minY;

      for(int var3 = 0; var3 < this.heightmap.getSize(); ++var3) {
         this.heightmap.set(var3, var2);
      }

   }

   private void set(int var1, int var2) {
      this.heightmap.set(var1, var2 - this.minY);
   }

   private int get(int var1) {
      return this.heightmap.get(var1) + this.minY;
   }

   private int extendSourcesBelowWorld(int var1) {
      return var1 == this.minY ? -2147483648 : var1;
   }

   private static int index(int var0, int var1) {
      return var0 + var1 * 16;
   }
}
