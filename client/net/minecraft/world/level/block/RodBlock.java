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

   protected RodBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected abstract MapCodec<? extends RodBlock> codec();

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)SHAPES.get(((Direction)state.getValue(FACING)).getAxis());
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return (BlockState)state.setValue(FACING, mirror.mirror((Direction)state.getValue(FACING)));
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }
}
