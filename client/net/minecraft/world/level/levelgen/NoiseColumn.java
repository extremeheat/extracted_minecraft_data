package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import java.util.BitSet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import org.jspecify.annotations.Nullable;

public class NoiseColumn {
   public static final @Nullable BlockState SOLID = null;
   public static final BlockState AIR;
   private final int minBlockY;
   final @Nullable BlockState[] blocks;
   final BitSet fluidUpdates;
   int topIndexY = -1;
   int surfaceGradientX;
   int surfaceGradientZ;

   public NoiseColumn(final int minBlockY, final int sizeY) {
      super();
      this.minBlockY = minBlockY;
      this.blocks = new BlockState[sizeY];
      Arrays.fill(this.blocks, AIR);
      this.fluidUpdates = new BitSet(sizeY);
   }

   public int minBlockY() {
      return this.minBlockY;
   }

   public int sizeY() {
      return this.blocks.length;
   }

   public int blockY(final int indexY) {
      return indexY + this.minBlockY;
   }

   public @Nullable BlockState getBlockForIndex(final int indexY) {
      return this.blocks[indexY];
   }

   public boolean scheduleFluidUpdateForIndex(final int indexY) {
      return this.fluidUpdates.get(indexY);
   }

   public int getCeilingBelowIndex(final int indexY) {
      for(int probeY = indexY - 1; probeY >= 0; --probeY) {
         if (this.getBlockForIndex(probeY) != SOLID) {
            return probeY + 1;
         }
      }

      return DimensionType.WAY_BELOW_MIN_Y;
   }

   public int topIndexY() {
      return this.topIndexY;
   }

   private void setNonEmptyBlock(final int blockY, final @Nullable BlockState block) {
      if (block == AIR) {
         throw new IllegalArgumentException("Cannot set air");
      } else {
         int indexY = blockY - this.minBlockY;
         this.blocks[indexY] = block;
         this.topIndexY = Math.max(this.topIndexY, indexY);
      }
   }

   public void setSolid(final int blockY) {
      this.setNonEmptyBlock(blockY, SOLID);
   }

   public void setFluid(final int blockY, final BlockState block) {
      this.setNonEmptyBlock(blockY, block);
   }

   public @Nullable BlockState getBlock(final int blockY) {
      int indexY = blockY - this.minBlockY;
      return indexY >= 0 && indexY < this.blocks.length ? this.blocks[indexY] : AIR;
   }

   public boolean isSolid(final int blockY) {
      return this.getBlock(blockY) == SOLID;
   }

   public boolean isEmpty(final int blockY) {
      return this.getBlock(blockY) == AIR;
   }

   public int findTopSolidBlockY() {
      for(int indexY = this.topIndexY; indexY >= 0; --indexY) {
         if (this.getBlockForIndex(indexY) == SOLID) {
            return this.minBlockY + indexY;
         }
      }

      return this.minBlockY - 1;
   }

   public int topBlockY() {
      return this.minBlockY + this.topIndexY;
   }

   public int surfaceGradientX() {
      return this.surfaceGradientX;
   }

   public int surfaceGradientZ() {
      return this.surfaceGradientZ;
   }

   static {
      AIR = Blocks.AIR.defaultBlockState();
   }
}
