package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class InfestedRotatedPillarBlock extends InfestedBlock {
   public InfestedRotatedPillarBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y));
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return RotatedPillarBlock.rotatePillar(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(RotatedPillarBlock.AXIS);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(RotatedPillarBlock.AXIS, var1.getClickedFace().getAxis());
   }
}
