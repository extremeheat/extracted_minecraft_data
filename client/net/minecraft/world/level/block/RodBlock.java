package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class RodBlock extends DirectionalBlock {
   private static final Map<Direction.Axis, VoxelShape> SHAPES = Shapes.rotateAllAxis(Block.cube(4.0, 4.0, 16.0));

   protected RodBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected abstract MapCodec<? extends RodBlock> codec();

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)SHAPES.get(((Direction)var1.getValue(FACING)).getAxis());
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
