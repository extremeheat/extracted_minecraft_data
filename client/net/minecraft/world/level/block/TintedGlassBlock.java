package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TintedGlassBlock extends AbstractGlassBlock {
   public TintedGlassBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   public int getLightBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var2.getMaxLightLevel();
   }
}
