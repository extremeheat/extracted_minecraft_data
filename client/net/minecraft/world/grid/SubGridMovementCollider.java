package net.minecraft.world.grid;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SubGridMovementCollider {
   private final LongList edgeBlocks;
   private final BlockPos size;

   private SubGridMovementCollider(LongList var1, BlockPos var2) {
      super();
      this.edgeBlocks = var1;
      this.size = var2;
   }

   public static SubGridMovementCollider generate(SubGridBlocks var0, Direction var1) {
      LongArrayList var2 = new LongArrayList();
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      Direction var4 = var1.getOpposite();

      for(BlockPos var6 : getFrontSide(var0, var1)) {
         var3.set(var6);
         boolean var7 = false;
         int var8 = var1.getAxis().choose(var0.sizeX(), var0.sizeY(), var0.sizeZ());

         for(int var9 = 0; var9 < var8; ++var9) {
            BlockState var10 = var0.getBlockState(var3);
            if (isCollidable(var10)) {
               if (!var7) {
                  var2.add(var3.asLong());
               }

               var7 = true;
            } else {
               var7 = false;
            }

            var3.move(var4);
         }
      }

      return new SubGridMovementCollider(var2, new BlockPos(var0.sizeX(), var0.sizeY(), var0.sizeZ()));
   }

   private static Iterable<BlockPos> getFrontSide(SubGridBlocks var0, Direction var1) {
      BlockPos var2 = new BlockPos(
         Math.max(var1.getStepX(), 0) * (var0.sizeX() - 1),
         Math.max(var1.getStepY(), 0) * (var0.sizeY() - 1),
         Math.max(var1.getStepZ(), 0) * (var0.sizeZ() - 1)
      );
      BlockPos var3 = var2.offset(
         var1.getAxis() == Direction.Axis.X ? 0 : var0.sizeX() - 1,
         var1.getAxis() == Direction.Axis.Y ? 0 : var0.sizeY() - 1,
         var1.getAxis() == Direction.Axis.Z ? 0 : var0.sizeZ() - 1
      );
      return BlockPos.betweenClosed(var2, var3);
   }

   public boolean checkCollision(Level var1, BlockPos var2) {
      int var3 = var2.getY();
      int var4 = var3 + this.size.getY() - 1;
      if (var3 >= var1.getMinBuildHeight() && var4 < var1.getMaxBuildHeight()) {
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
         LongIterator var6 = this.edgeBlocks.longIterator();

         while(var6.hasNext()) {
            var5.set(var6.nextLong());
            var5.move(var2);
            if (isCollidable(var1.getBlockState(var5))) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private static boolean isCollidable(BlockState var0) {
      return !var0.canBeReplaced();
   }
}
