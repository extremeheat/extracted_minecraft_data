package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SnowyDirtBlock extends Block {
   public static final BooleanProperty SNOWY;

   protected SnowyDirtBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(SNOWY, false));
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 != Direction.UP) {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         Block var7 = var3.getBlock();
         return (BlockState)var1.setValue(SNOWY, var7 == Blocks.SNOW_BLOCK || var7 == Blocks.SNOW);
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Block var2 = var1.getLevel().getBlockState(var1.getClickedPos().above()).getBlock();
      return (BlockState)this.defaultBlockState().setValue(SNOWY, var2 == Blocks.SNOW_BLOCK || var2 == Blocks.SNOW);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(SNOWY);
   }

   static {
      SNOWY = BlockStateProperties.SNOWY;
   }
}
