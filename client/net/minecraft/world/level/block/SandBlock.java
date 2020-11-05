package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SandBlock extends FallingBlock {
   private final int dustColor;

   public SandBlock(int var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.dustColor = var1;
   }

   public int getDustColor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.dustColor;
   }
}
