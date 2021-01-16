package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class SandBlock extends FallingBlock {
   private final int dustColor;

   public SandBlock(int var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.dustColor = var1;
   }
}
