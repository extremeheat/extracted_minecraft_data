package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public record BlockEventData(BlockPos a, Block b, int c, int d) {
   private final BlockPos pos;
   private final Block block;
   private final int paramA;
   private final int paramB;

   public BlockEventData(BlockPos var1, Block var2, int var3, int var4) {
      super();
      this.pos = var1;
      this.block = var2;
      this.paramA = var3;
      this.paramB = var4;
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
