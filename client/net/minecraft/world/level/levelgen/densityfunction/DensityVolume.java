package net.minecraft.world.level.levelgen.densityfunction;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record DensityVolume(int sizeX, int sizeY, int sizeZ, int minBlockX, int minBlockY, int minBlockZ, int stepBlockX, int stepBlockY, int stepBlockZ) {
   public static final int NO_BLOCK = -1;

   public DensityVolume {
      super();
      if (sizeX > 0 && sizeY > 0 && sizeZ > 0) {
         if (stepBlockX > 0 && stepBlockY > 0 && stepBlockZ > 0) {
            if ((long)sizeX * (long)sizeY * (long)sizeZ > 2147483647L) {
               throw new IllegalArgumentException("Volume too large, was: " + sizeX + "x" + sizeY + "x" + sizeZ);
            }
         } else {
            throw new IllegalArgumentException("Step must be positive, was: " + stepBlockX + "; " + stepBlockY + "; " + stepBlockZ);
         }
      } else {
         throw new IllegalArgumentException("Size must be positive, was: " + sizeX + "x" + sizeY + "x" + sizeZ);
      }
   }

   public DensityVolume(final int sizeX, final int sizeY, final int sizeZ, final int minBlockX, final int minBlockY, final int minBlockZ) {
      this(sizeX, sizeY, sizeZ, minBlockX, minBlockY, minBlockZ, 1, 1, 1);
   }

   public boolean fitsOnGrid(final int stepBlockX, final int stepBlockY, final int stepBlockZ) {
      return (this.sizeX == 1 || Mth.isMultipleOf(this.stepBlockX, stepBlockX)) && (this.sizeY == 1 || Mth.isMultipleOf(this.stepBlockY, stepBlockY)) && (this.sizeZ == 1 || Mth.isMultipleOf(this.stepBlockZ, stepBlockZ)) && Math.floorMod(this.minBlockX, stepBlockX) == 0 && Math.floorMod(this.minBlockY, stepBlockY) == 0 && Math.floorMod(this.minBlockZ, stepBlockZ) == 0;
   }

   public int indexUnchecked(final int indexX, final int indexY, final int indexZ) {
      return indexY + (indexX + indexZ * this.sizeX) * this.sizeY;
   }

   public int blockX(final int x) {
      return this.minBlockX + x * this.stepBlockX;
   }

   public int blockY(final int y) {
      return this.minBlockY + y * this.stepBlockY;
   }

   public int blockZ(final int z) {
      return this.minBlockZ + z * this.stepBlockZ;
   }

   public int maxBlockX() {
      return this.blockX(this.sizeX - 1);
   }

   public int maxBlockY() {
      return this.blockY(this.sizeY - 1);
   }

   public int maxBlockZ() {
      return this.blockZ(this.sizeZ - 1);
   }

   public int size() {
      return this.sizeX * this.sizeY * this.sizeZ;
   }

   public int strideX() {
      return this.sizeY;
   }

   public int strideY() {
      return 1;
   }

   public int strideZ() {
      return this.sizeX * this.sizeY;
   }

   public boolean intersects(final BoundingBox box) {
      return box.intersects(this.minBlockX, this.minBlockY, this.minBlockZ, this.maxBlockX(), this.maxBlockY(), this.maxBlockZ());
   }

   private boolean containsBlockRelative(final int relativeX, final int relativeY, final int relativeZ) {
      return relativeX >= 0 && relativeY >= 0 && relativeZ >= 0 && relativeX < this.sizeX * this.stepBlockX && relativeY < this.sizeY * this.stepBlockY && relativeZ < this.sizeZ * this.stepBlockZ && Math.floorMod(relativeX, this.stepBlockX) == 0 && Math.floorMod(relativeY, this.stepBlockY) == 0 && Math.floorMod(relativeZ, this.stepBlockZ) == 0;
   }

   public int indexOfBlock(final int blockX, final int blockY, final int blockZ) {
      int relativeX = blockX - this.minBlockX;
      int relativeY = blockY - this.minBlockY;
      int relativeZ = blockZ - this.minBlockZ;
      if (this.stepBlockX == 1 && this.stepBlockY == 1 && this.stepBlockZ == 1) {
         if (relativeX >= 0 && relativeY >= 0 && relativeZ >= 0 && relativeX < this.sizeX && relativeY < this.sizeY && relativeZ < this.sizeZ) {
            return this.indexUnchecked(relativeX, relativeY, relativeZ);
         }
      } else if (this.containsBlockRelative(relativeX, relativeY, relativeZ)) {
         return this.indexUnchecked(Mth.floorDiv(relativeX, this.stepBlockX), Mth.floorDiv(relativeY, this.stepBlockY), Mth.floorDiv(relativeZ, this.stepBlockZ));
      }

      return -1;
   }
}
