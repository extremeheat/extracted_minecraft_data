package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class CrossCollisionBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty WATERLOGGED;
   public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
   private final Function<BlockState, VoxelShape> collisionShapes;
   private final Function<BlockState, VoxelShape> shapes;

   protected CrossCollisionBlock(final float postWidth, final float postHeight, final float wallWidth, final float wallHeight, final float collisionHeight, final BlockBehaviour.Properties properties) {
      super(properties);
      this.collisionShapes = this.makeShapes(postWidth, collisionHeight, wallWidth, 0.0F, collisionHeight);
      this.shapes = this.makeShapes(postWidth, postHeight, wallWidth, 0.0F, wallHeight);
   }

   protected Function<BlockState, VoxelShape> makeShapes(final float postWidth, final float postHeight, final float wallWidth, final float wallBottom, final float wallTop) {
      VoxelShape post = Block.column((double)postWidth, 0.0, (double)postHeight);
      Map<Direction, VoxelShape> arms = Shapes.rotateHorizontal(Block.boxZ((double)wallWidth, (double)wallBottom, (double)wallTop, 0.0, 8.0));
      return this.getShapeForEachState((state) -> {
         VoxelShape shape = post;

         for(Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
            if ((Boolean)state.getValue((Property)entry.getValue())) {
               shape = Shapes.or(shape, (VoxelShape)arms.get(entry.getKey()));
            }
         }

         return shape;
      }, new Property[]{WATERLOGGED});
   }

   protected boolean propagatesSkylightDown(final BlockState state) {
      return !(Boolean)state.getValue(WATERLOGGED);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.shapes.apply(state);
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.collisionShapes.apply(state);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      BlockState var10000;
      switch (rotation) {
         case CLOCKWISE_180 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (Boolean)state.getValue(SOUTH))).setValue(EAST, (Boolean)state.getValue(WEST))).setValue(SOUTH, (Boolean)state.getValue(NORTH))).setValue(WEST, (Boolean)state.getValue(EAST));
         case COUNTERCLOCKWISE_90 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (Boolean)state.getValue(EAST))).setValue(EAST, (Boolean)state.getValue(SOUTH))).setValue(SOUTH, (Boolean)state.getValue(WEST))).setValue(WEST, (Boolean)state.getValue(NORTH));
         case CLOCKWISE_90 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (Boolean)state.getValue(WEST))).setValue(EAST, (Boolean)state.getValue(NORTH))).setValue(SOUTH, (Boolean)state.getValue(EAST))).setValue(WEST, (Boolean)state.getValue(SOUTH));
         default -> var10000 = state;
      }

      return var10000;
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      switch (mirror) {
         case LEFT_RIGHT -> {
            return (BlockState)((BlockState)state.setValue(NORTH, (Boolean)state.getValue(SOUTH))).setValue(SOUTH, (Boolean)state.getValue(NORTH));
         }
         case FRONT_BACK -> {
            return (BlockState)((BlockState)state.setValue(EAST, (Boolean)state.getValue(WEST))).setValue(WEST, (Boolean)state.getValue(EAST));
         }
         default -> {
            return super.mirror(state, mirror);
         }
      }
   }

   static {
      NORTH = PipeBlock.NORTH;
      EAST = PipeBlock.EAST;
      SOUTH = PipeBlock.SOUTH;
      WEST = PipeBlock.WEST;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((e) -> ((Direction)e.getKey()).getAxis().isHorizontal()).collect(Util.toMap());
   }
}
