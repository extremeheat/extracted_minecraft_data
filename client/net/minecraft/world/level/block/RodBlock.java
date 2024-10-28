package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class RodBlock extends DirectionalBlock {
   protected static final float AABB_MIN = 6.0F;
   protected static final float AABB_MAX = 10.0F;
   protected static final VoxelShape Y_AXIS_AABB = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);

   protected RodBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected abstract MapCodec<? extends RodBlock> codec();

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch (((Direction)var1.getValue(FACING)).getAxis()) {
         case X:
         default:
            return X_AXIS_AABB;
         case Z:
            return Z_AXIS_AABB;
         case Y:
            return Y_AXIS_AABB;
      }
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return (BlockState)var1.setValue(FACING, var2.mirror((Direction)var1.getValue(FACING)));
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
