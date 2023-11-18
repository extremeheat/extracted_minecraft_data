package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface SignalGetter extends BlockGetter {
   Direction[] DIRECTIONS = Direction.values();

   default int getDirectSignal(BlockPos var1, Direction var2) {
      return this.getBlockState(var1).getDirectSignal(this, var1, var2);
   }

   default int getDirectSignalTo(BlockPos var1) {
      int var2 = 0;
      var2 = Math.max(var2, this.getDirectSignal(var1.below(), Direction.DOWN));
      if (var2 >= 15) {
         return var2;
      } else {
         var2 = Math.max(var2, this.getDirectSignal(var1.above(), Direction.UP));
         if (var2 >= 15) {
            return var2;
         } else {
            var2 = Math.max(var2, this.getDirectSignal(var1.north(), Direction.NORTH));
            if (var2 >= 15) {
               return var2;
            } else {
               var2 = Math.max(var2, this.getDirectSignal(var1.south(), Direction.SOUTH));
               if (var2 >= 15) {
                  return var2;
               } else {
                  var2 = Math.max(var2, this.getDirectSignal(var1.west(), Direction.WEST));
                  if (var2 >= 15) {
                     return var2;
                  } else {
                     var2 = Math.max(var2, this.getDirectSignal(var1.east(), Direction.EAST));
                     return var2 >= 15 ? var2 : var2;
                  }
               }
            }
         }
      }
   }

   default int getControlInputSignal(BlockPos var1, Direction var2, boolean var3) {
      BlockState var4 = this.getBlockState(var1);
      if (var3) {
         return DiodeBlock.isDiode(var4) ? this.getDirectSignal(var1, var2) : 0;
      } else if (var4.is(Blocks.REDSTONE_BLOCK)) {
         return 15;
      } else if (var4.is(Blocks.REDSTONE_WIRE)) {
         return var4.getValue(RedStoneWireBlock.POWER);
      } else {
         return var4.isSignalSource() ? this.getDirectSignal(var1, var2) : 0;
      }
   }

   default boolean hasSignal(BlockPos var1, Direction var2) {
      return this.getSignal(var1, var2) > 0;
   }

   default int getSignal(BlockPos var1, Direction var2) {
      BlockState var3 = this.getBlockState(var1);
      int var4 = var3.getSignal(this, var1, var2);
      return var3.isRedstoneConductor(this, var1) ? Math.max(var4, this.getDirectSignalTo(var1)) : var4;
   }

   default boolean hasNeighborSignal(BlockPos var1) {
      if (this.getSignal(var1.below(), Direction.DOWN) > 0) {
         return true;
      } else if (this.getSignal(var1.above(), Direction.UP) > 0) {
         return true;
      } else if (this.getSignal(var1.north(), Direction.NORTH) > 0) {
         return true;
      } else if (this.getSignal(var1.south(), Direction.SOUTH) > 0) {
         return true;
      } else if (this.getSignal(var1.west(), Direction.WEST) > 0) {
         return true;
      } else {
         return this.getSignal(var1.east(), Direction.EAST) > 0;
      }
   }

   default int getBestNeighborSignal(BlockPos var1) {
      int var2 = 0;

      for(Direction var6 : DIRECTIONS) {
         int var7 = this.getSignal(var1.relative(var6), var6);
         if (var7 >= 15) {
            return 15;
         }

         if (var7 > var2) {
            var2 = var7;
         }
      }

      return var2;
   }
}
