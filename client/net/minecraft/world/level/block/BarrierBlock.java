package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BarrierBlock extends Block {
   protected BarrierBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.INVISIBLE;
   }

   @Override
   public float getShadeBrightness(BlockState var1, BlockGetter var2, BlockPos var3) {
      return 1.0F;
   }
}
