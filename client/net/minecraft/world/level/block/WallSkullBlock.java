package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallSkullBlock extends AbstractSkullBlock {
   public static final EnumProperty<Direction> FACING;
   private static final Map<Direction, VoxelShape> SHAPES;

   protected WallSkullBlock(final SkullBlock.Type type, final BlockBehaviour.Properties properties) {
      super(type, properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)SHAPES.get(state.getValue(FACING));
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      BlockGetter level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      Direction[] directions = context.getNearestLookingDirections();

      for(Direction direction : directions) {
         if (direction.getAxis().isHorizontal()) {
            Direction facing = direction.getOpposite();
            state = (BlockState)state.setValue(FACING, facing);
            if (!level.getBlockState(pos.relative(direction)).canBeReplaced(context)) {
               return state;
            }
         }
      }

      return null;
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(FACING);
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      SHAPES = Shapes.rotateHorizontal(Block.boxZ(8.0, 8.0, 16.0));
   }
}
