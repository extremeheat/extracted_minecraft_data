package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SnowyDirtBlock extends Block {
   public static final BooleanProperty SNOWY;

   protected SnowyDirtBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(SNOWY, false));
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 != Direction.UP ? super.updateShape(var1, var2, var3, var4, var5, var6) : (BlockState)var1.setValue(SNOWY, var3.is(Blocks.SNOW_BLOCK) || var3.is(Blocks.SNOW));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos().above());
      return (BlockState)this.defaultBlockState().setValue(SNOWY, var2.is(Blocks.SNOW_BLOCK) || var2.is(Blocks.SNOW));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(SNOWY);
   }

   static {
      SNOWY = BlockStateProperties.SNOWY;
   }
}
