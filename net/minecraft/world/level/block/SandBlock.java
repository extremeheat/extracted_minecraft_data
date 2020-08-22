package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockState;

public class SandBlock extends FallingBlock {
   private final int dustColor;

   public SandBlock(int var1, Block.Properties var2) {
      super(var2);
      this.dustColor = var1;
   }

   public int getDustColor(BlockState var1) {
      return this.dustColor;
   }
}
