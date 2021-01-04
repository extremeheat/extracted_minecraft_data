package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RotatedPillarBlock extends Block {
   public static final EnumProperty<Direction.Axis> AXIS;

   public RotatedPillarBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      switch(var2) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Direction.Axis)var1.getValue(AXIS)) {
         case X:
            return (BlockState)var1.setValue(AXIS, Direction.Axis.Z);
         case Z:
            return (BlockState)var1.setValue(AXIS, Direction.Axis.X);
         default:
            return var1;
         }
      default:
         return var1;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AXIS);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(AXIS, var1.getClickedFace().getAxis());
   }

   static {
      AXIS = BlockStateProperties.AXIS;
   }
}
