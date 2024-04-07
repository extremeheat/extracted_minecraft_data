package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class GlazedTerracottaBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<GlazedTerracottaBlock> CODEC = simpleCodec(GlazedTerracottaBlock::new);

   @Override
   public MapCodec<GlazedTerracottaBlock> codec() {
      return CODEC;
   }

   public GlazedTerracottaBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }
}
