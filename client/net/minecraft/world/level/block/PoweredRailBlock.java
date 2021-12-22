package net.minecraft.world.level.block;

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
   public static final EnumProperty<RailShape> SHAPE;
   public static final BooleanProperty POWERED;

   protected PoweredRailBlock(BlockBehaviour.Properties var1) {
      super(true, var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(POWERED, false)).setValue(WATERLOGGED, false));
   }

   protected boolean findPoweredRailSignal(Level var1, BlockPos var2, BlockState var3, boolean var4, int var5) {
      if (var5 >= 8) {
         return false;
      } else {
         int var6 = var2.getX();
         int var7 = var2.getY();
         int var8 = var2.getZ();
         boolean var9 = true;
         RailShape var10 = (RailShape)var3.getValue(SHAPE);
         switch(var10) {
         case NORTH_SOUTH:
            if (var4) {
               ++var8;
            } else {
               --var8;
            }
            break;
         case EAST_WEST:
            if (var4) {
               --var6;
            } else {
               ++var6;
            }
            break;
         case ASCENDING_EAST:
            if (var4) {
               --var6;
            } else {
               ++var6;
               ++var7;
               var9 = false;
            }

            var10 = RailShape.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if (var4) {
               --var6;
               ++var7;
               var9 = false;
            } else {
               ++var6;
            }

            var10 = RailShape.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if (var4) {
               ++var8;
            } else {
               --var8;
               ++var7;
               var9 = false;
            }

            var10 = RailShape.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if (var4) {
               ++var8;
               ++var7;
               var9 = false;
            } else {
               --var8;
            }

            var10 = RailShape.NORTH_SOUTH;
         }

         if (this.isSameRailWithPower(var1, new BlockPos(var6, var7, var8), var4, var5, var10)) {
            return true;
         } else {
            return var9 && this.isSameRailWithPower(var1, new BlockPos(var6, var7 - 1, var8), var4, var5, var10);
         }
      }
   }

   protected boolean isSameRailWithPower(Level var1, BlockPos var2, boolean var3, int var4, RailShape var5) {
      BlockState var6 = var1.getBlockState(var2);
      if (!var6.is(this)) {
         return false;
      } else {
         RailShape var7 = (RailShape)var6.getValue(SHAPE);
         if (var5 == RailShape.EAST_WEST && (var7 == RailShape.NORTH_SOUTH || var7 == RailShape.ASCENDING_NORTH || var7 == RailShape.ASCENDING_SOUTH)) {
            return false;
         } else if (var5 == RailShape.NORTH_SOUTH && (var7 == RailShape.EAST_WEST || var7 == RailShape.ASCENDING_EAST || var7 == RailShape.ASCENDING_WEST)) {
            return false;
         } else if ((Boolean)var6.getValue(POWERED)) {
            return var1.hasNeighborSignal(var2) ? true : this.findPoweredRailSignal(var1, var2, var6, var3, var4 + 1);
         } else {
            return false;
         }
      }
   }

   protected void updateState(BlockState var1, Level var2, BlockPos var3, Block var4) {
      boolean var5 = (Boolean)var1.getValue(POWERED);
      boolean var6 = var2.hasNeighborSignal(var3) || this.findPoweredRailSignal(var2, var3, var1, true, 0) || this.findPoweredRailSignal(var2, var3, var1, false, 0);
      if (var6 != var5) {
         var2.setBlock(var3, (BlockState)var1.setValue(POWERED, var6), 3);
         var2.updateNeighborsAt(var3.below(), this);
         if (((RailShape)var1.getValue(SHAPE)).isAscending()) {
            var2.updateNeighborsAt(var3.above(), this);
         }
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
         case NORTH_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_SOUTH);
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
         }
      case CLOCKWISE_90:
         switch((RailShape)var1.getValue(SHAPE)) {
         case NORTH_SOUTH:
            return (BlockState)var1.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_SOUTH);
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
      var1.add(SHAPE, POWERED, WATERLOGGED);
   }

   static {
      SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
      POWERED = BlockStateProperties.POWERED;
   }
}
