package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public record BlockEventData(BlockPos pos, Block block, int paramA, int paramB) {
   public BlockEventData(BlockPos pos, Block block, int paramA, int paramB) {
      super();
      this.pos = pos;
      this.block = block;
      this.paramA = paramA;
      this.paramB = paramB;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public Block block() {
      return this.block;
   }

   public int paramA() {
      return this.paramA;
   }

   public int paramB() {
      return this.paramB;
   }
}
