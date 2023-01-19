package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum SupportType {
   FULL {
      @Override
      public boolean isSupporting(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
         return Block.isFaceFull(var1.getBlockSupportShape(var2, var3), var4);
      }
   },
   CENTER {
      private final int CENTER_SUPPORT_WIDTH = 1;
      private final VoxelShape CENTER_SUPPORT_SHAPE = Block.box(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);

      @Override
      public boolean isSupporting(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
         return !Shapes.joinIsNotEmpty(var1.getBlockSupportShape(var2, var3).getFaceShape(var4), this.CENTER_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
      }
   },
   RIGID {
      private final int RIGID_SUPPORT_WIDTH = 2;
      private final VoxelShape RIGID_SUPPORT_SHAPE = Shapes.join(Shapes.block(), Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);

      @Override
      public boolean isSupporting(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
         return !Shapes.joinIsNotEmpty(var1.getBlockSupportShape(var2, var3).getFaceShape(var4), this.RIGID_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
      }
   };

   SupportType() {
   }

   public abstract boolean isSupporting(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4);
}