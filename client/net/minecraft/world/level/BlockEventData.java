package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public record BlockEventData(BlockPos pos, Block block, int paramA, int paramB) {
   public BlockEventData(BlockPos var1, Block var2, int var3, int var4) {
      super();
      this.pos = var1;
      this.block = var2;
      this.paramA = var3;
      this.paramB = var4;
   }
}
