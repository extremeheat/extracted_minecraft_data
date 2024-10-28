package net.minecraft.world.level;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;

public final class NoiseColumn implements BlockColumn {
   private final int minY;
   private final BlockState[] column;

   public NoiseColumn(int var1, BlockState[] var2) {
      super();
      this.minY = var1;
      this.column = var2;
   }

   public BlockState getBlock(int var1) {
      int var2 = var1 - this.minY;
      return var2 >= 0 && var2 < this.column.length ? this.column[var2] : Blocks.AIR.defaultBlockState();
   }

   public void setBlock(int var1, BlockState var2) {
      int var3 = var1 - this.minY;
      if (var3 >= 0 && var3 < this.column.length) {
         this.column[var3] = var2;
      } else {
         throw new IllegalArgumentException("Outside of column height: " + var1);
      }
   }
}
