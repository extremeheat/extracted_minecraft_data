package net.minecraft.world.level.levelgen.densityfunction;

public record DensityVolume(int sizeX, int sizeY, int sizeZ, int minBlockX, int minBlockY, int minBlockZ) {
   public DensityVolume {
      super();
   }

   public int indexUnchecked(final int indexX, final int indexY, final int indexZ) {
      return indexY + (indexX + indexZ * this.sizeX) * this.sizeY;
   }

   public int blockX(final int indexX) {
      return this.minBlockX + indexX;
   }

   public int blockY(final int indexY) {
      return this.minBlockY + indexY;
   }

   public int blockZ(final int indexZ) {
      return this.minBlockZ + indexZ;
   }

   public int size() {
      return this.sizeX * this.sizeY * this.sizeZ;
   }
}
