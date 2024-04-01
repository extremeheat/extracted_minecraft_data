package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class PotatoZombieHeadBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<PotatoZombieHeadBlock> CODEC = simpleCodec(PotatoZombieHeadBlock::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

   @Override
   public MapCodec<? extends PotatoZombieHeadBlock> codec() {
      return CODEC;
   }

   protected PotatoZombieHeadBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }
}
