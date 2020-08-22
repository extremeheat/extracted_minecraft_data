package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;

public abstract class ScatteredFeaturePiece extends StructurePiece {
   protected final int width;
   protected final int height;
   protected final int depth;
   protected int heightPosition = -1;

   protected ScatteredFeaturePiece(StructurePieceType var1, Random var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      super(var1, 0);
      this.width = var6;
      this.height = var7;
      this.depth = var8;
      this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(var2));
      if (this.getOrientation().getAxis() == Direction.Axis.Z) {
         this.boundingBox = new BoundingBox(var3, var4, var5, var3 + var6 - 1, var4 + var7 - 1, var5 + var8 - 1);
      } else {
         this.boundingBox = new BoundingBox(var3, var4, var5, var3 + var8 - 1, var4 + var7 - 1, var5 + var6 - 1);
      }

   }

   protected ScatteredFeaturePiece(StructurePieceType var1, CompoundTag var2) {
      super(var1, var2);
      this.width = var2.getInt("Width");
      this.height = var2.getInt("Height");
      this.depth = var2.getInt("Depth");
      this.heightPosition = var2.getInt("HPos");
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putInt("Width", this.width);
      var1.putInt("Height", this.height);
      var1.putInt("Depth", this.depth);
      var1.putInt("HPos", this.heightPosition);
   }

   protected boolean updateAverageGroundHeight(LevelAccessor var1, BoundingBox var2, int var3) {
      if (this.heightPosition >= 0) {
         return true;
      } else {
         int var4 = 0;
         int var5 = 0;
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

         for(int var7 = this.boundingBox.z0; var7 <= this.boundingBox.z1; ++var7) {
            for(int var8 = this.boundingBox.x0; var8 <= this.boundingBox.x1; ++var8) {
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
            this.boundingBox.move(0, this.heightPosition - this.boundingBox.y0 + var3, 0);
            return true;
         }
      }
   }
}
