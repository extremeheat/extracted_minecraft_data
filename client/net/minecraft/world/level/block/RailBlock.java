package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class RailBlock extends BaseRailBlock {
   public static final EnumProperty<RailShape> SHAPE;

   protected RailBlock(BlockBehaviour.Properties var1) {
      super(false, var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, false));
   }

   protected void updateState(BlockState var1, Level var2, BlockPos var3, Block var4) {
      if (var4.defaultBlockState().isSignalSource() && (new RailState(var2, var3, var1)).countPotentialConnections() == 3) {
         this.updateDir(var2, var3, var1, false);
      }

   }

   public Property<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      switch(var2) {
      case CLOCKWISE_180:
         switch((RailShape)var1.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)var1.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape)var1.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_SOUTH);
         }
      default:
         return var1;
      }
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      RailShape var3 = (RailShape)var1.getValue(SHAPE);
      switch(var2) {
      case LEFT_RIGHT:
         switch(var3) {
         case ASCENDING_NORTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(var1, var2);
         }
      case FRONT_BACK:
         switch(var3) {
         case ASCENDING_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
         }
      }

      return super.mirror(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(SHAPE, WATERLOGGED);
   }

   static {
      SHAPE = BlockStateProperties.RAIL_SHAPE;
   }
}
