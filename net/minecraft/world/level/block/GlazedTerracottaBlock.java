package net.minecraft.world.level.block;

import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;

public class GlazedTerracottaBlock extends HorizontalDirectionalBlock {
   public GlazedTerracottaBlock(Block.Properties var1) {
      super(var1);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(FACING);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.PUSH_ONLY;
   }
}
