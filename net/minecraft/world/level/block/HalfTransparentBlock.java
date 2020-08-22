package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class HalfTransparentBlock extends Block {
   protected HalfTransparentBlock(Block.Properties var1) {
      super(var1);
   }

   public boolean skipRendering(BlockState var1, BlockState var2, Direction var3) {
      return var2.getBlock() == this ? true : super.skipRendering(var1, var2, var3);
   }
}
