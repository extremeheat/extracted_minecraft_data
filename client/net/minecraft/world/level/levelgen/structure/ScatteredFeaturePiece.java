package net.minecraft.world.level.levelgen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public abstract class ScatteredFeaturePiece extends StructurePiece {
   protected final int width;
   protected final int height;
   protected final int depth;
   protected int heightPosition = -1;

   protected ScatteredFeaturePiece(StructurePieceType var1, int var2, int var3, int var4, int var5, int var6, int var7, Direction var8) {
      super(var1, 0, StructurePiece.makeBoundingBox(var2, var3, var4, var8, var5, var6, var7));
      this.width = var5;
      this.height = var6;
      this.depth = var7;
      this.setOrientation(var8);
   }

   protected ScatteredFeaturePiece(StructurePieceType var1, CompoundTag var2) {
      super(var1, var2);
      this.width = var2.getInt("Width");
      this.height = var2.getInt("Height");
      this.depth = var2.getInt("Depth");
      this.heightPosition = var2.getInt("HPos");
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
      var2.putInt("Width", this.width);
      var2.putInt("Height", this.height);
      var2.putInt("Depth", this.depth);
      var2.putInt("HPos", this.heightPosition);
   }

   protected boolean updateAverageGroundHeight(LevelAccessor var1, BoundingBox var2, int var3) {
      if (this.heightPosition >= 0) {
         return true;
      } else {
         int var4 = 0;
         int var5 = 0;
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

         for(int var7 = this.boundingBox.minZ(); var7 <= this.boundingBox.maxZ(); ++var7) {
            for(int var8 = this.boundingBox.minX(); var8 <= this.boundingBox.maxX(); ++var8) {
               var6.set(var8, 64, var7);
               if (var2.isInside(var6)) {
                  var4 += var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var6).getY();
                  ++var5;
               }
            }
         }

         if (var5 == 0) {
            return false;
         } else {
            this.heightPosition = var4 / var5;
            this.boundingBox.move(0, this.heightPosition - this.boundingBox.minY() + var3, 0);
            return true;
         }
      }
   }

   protected boolean updateHeightPositionToLowestGroundHeight(LevelAccessor var1, int var2) {
      if (this.heightPosition >= 0) {
         return true;
      } else {
         int var3 = var1.getMaxBuildHeight();
         boolean var4 = false;
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

         for(int var6 = this.boundingBox.minZ(); var6 <= this.boundingBox.maxZ(); ++var6) {
            for(int var7 = this.boundingBox.minX(); var7 <= this.boundingBox.maxX(); ++var7) {
               var5.set(var7, 0, var6);
               var3 = Math.min(var3, var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var5).getY());
               var4 = true;
            }
         }

         if (!var4) {
            return false;
         } else {
            this.heightPosition = var3;
            this.boundingBox.move(0, this.heightPosition - this.boundingBox.minY() + var2, 0);
            return true;
         }
      }
   }
}
