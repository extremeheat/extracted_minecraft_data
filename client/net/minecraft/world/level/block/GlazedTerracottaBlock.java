package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class GlazedTerracottaBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<GlazedTerracottaBlock> CODEC = simpleCodec(GlazedTerracottaBlock::new);

   public MapCodec<GlazedTerracottaBlock> codec() {
      return CODEC;
   }

   public GlazedTerracottaBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }
}
