package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class BushBlock extends Block {
   protected BushBlock(Block.Properties var1) {
      super(var1);
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      Block var4 = var1.getBlock();
      return var4 == Blocks.GRASS_BLOCK || var4 == Blocks.DIRT || var4 == Blocks.COARSE_DIRT || var4 == Blocks.PODZOL || var4 == Blocks.FARMLAND;
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      return this.mayPlaceOn(var2.getBlockState(var4), var2, var4);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }
}
