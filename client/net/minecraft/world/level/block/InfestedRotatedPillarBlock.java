package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class InfestedRotatedPillarBlock extends InfestedBlock {
   public InfestedRotatedPillarBlock(final Block hostBlock, final BlockBehaviour.Properties properties) {
      super(hostBlock, properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y));
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return RotatedPillarBlock.rotatePillar(state, rotation);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(RotatedPillarBlock.AXIS);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(RotatedPillarBlock.AXIS, context.getClickedFace().getAxis());
   }
}
