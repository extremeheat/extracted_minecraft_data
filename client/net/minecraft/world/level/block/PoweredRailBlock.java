package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class PoweredRailBlock extends BaseRailBlock {
   public static final MapCodec<PoweredRailBlock> CODEC = simpleCodec(PoweredRailBlock::new);
   public static final EnumProperty<RailShape> SHAPE;
   public static final BooleanProperty POWERED;

   public MapCodec<PoweredRailBlock> codec() {
      return CODEC;
   }

   protected PoweredRailBlock(final BlockBehaviour.Properties properties) {
      super(true, properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(POWERED, false)).setValue(WATERLOGGED, false));
   }

   protected boolean findPoweredRailSignal(final Level level, final BlockPos pos, final BlockState state, final boolean forward, final int searchDepth) {
      if (searchDepth >= 8) {
         return false;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         boolean checkBelow = true;
         RailShape shape = (RailShape)state.getValue(SHAPE);
         switch (shape) {
            case NORTH_SOUTH:
               if (forward) {
                  ++z;
               } else {
                  --z;
               }
               break;
            case EAST_WEST:
               if (forward) {
                  --x;
               } else {
                  ++x;
               }
               break;
            case ASCENDING_EAST:
               if (forward) {
                  --x;
               } else {
                  ++x;
                  ++y;
                  checkBelow = false;
               }

               shape = RailShape.EAST_WEST;
               break;
            case ASCENDING_WEST:
               if (forward) {
                  --x;
                  ++y;
                  checkBelow = false;
               } else {
                  ++x;
               }

               shape = RailShape.EAST_WEST;
               break;
            case ASCENDING_NORTH:
               if (forward) {
                  ++z;
               } else {
                  --z;
                  ++y;
                  checkBelow = false;
               }

               shape = RailShape.NORTH_SOUTH;
               break;
            case ASCENDING_SOUTH:
               if (forward) {
                  ++z;
                  ++y;
                  checkBelow = false;
               } else {
                  --z;
               }

               shape = RailShape.NORTH_SOUTH;
         }

         if (this.isSameRailWithPower(level, new BlockPos(x, y, z), forward, searchDepth, shape)) {
            return true;
         } else {
            return checkBelow && this.isSameRailWithPower(level, new BlockPos(x, y - 1, z), forward, searchDepth, shape);
         }
      }
   }

   protected boolean isSameRailWithPower(final Level level, final BlockPos pos, final boolean forward, final int searchDepth, final RailShape dir) {
      BlockState state = level.getBlockState(pos);
      if (!state.is(this)) {
         return false;
      } else {
         RailShape myShape = (RailShape)state.getValue(SHAPE);
         if (dir != RailShape.EAST_WEST || myShape != RailShape.NORTH_SOUTH && myShape != RailShape.ASCENDING_NORTH && myShape != RailShape.ASCENDING_SOUTH) {
            if (dir != RailShape.NORTH_SOUTH || myShape != RailShape.EAST_WEST && myShape != RailShape.ASCENDING_EAST && myShape != RailShape.ASCENDING_WEST) {
               if ((Boolean)state.getValue(POWERED)) {
                  return level.hasNeighborSignal(pos) ? true : this.findPoweredRailSignal(level, pos, state, forward, searchDepth + 1);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected void updateState(final BlockState state, final Level level, final BlockPos pos, final Block block) {
      boolean isPowered = (Boolean)state.getValue(POWERED);
      boolean shouldPower = level.hasNeighborSignal(pos) || this.findPoweredRailSignal(level, pos, state, true, 0) || this.findPoweredRailSignal(level, pos, state, false, 0);
      if (shouldPower != isPowered) {
         level.setBlock(pos, (BlockState)state.setValue(POWERED, shouldPower), 3);
         level.updateNeighborsAt(pos.below(), this);
         if (((RailShape)state.getValue(SHAPE)).isSlope()) {
            level.updateNeighborsAt(pos.above(), this);
         }
      }

   }

   public Property<RailShape> getShapeProperty() {
      return SHAPE;
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      RailShape currentShape = (RailShape)state.getValue(SHAPE);
      RailShape newShape = this.rotate(currentShape, rotation);
      return (BlockState)state.setValue(SHAPE, newShape);
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      RailShape currentShape = (RailShape)state.getValue(SHAPE);
      RailShape newShape = this.mirror(currentShape, mirror);
      return (BlockState)state.setValue(SHAPE, newShape);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(SHAPE, POWERED, WATERLOGGED);
   }

   static {
      SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
      POWERED = BlockStateProperties.POWERED;
   }
}
