package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class BlockEventData {
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

   public BlockPos getPos() {
      return this.pos;
   }

   public Block getBlock() {
      return this.block;
   }

   public int getParamA() {
      return this.paramA;
   }

   public int getParamB() {
      return this.paramB;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof BlockEventData)) {
         return false;
      } else {
         BlockEventData var2 = (BlockEventData)var1;
         return this.pos.equals(var2.pos) && this.paramA == var2.paramA && this.paramB == var2.paramB && this.block == var2.block;
      }
   }

   public String toString() {
      return "TE(" + this.pos + ")," + this.paramA + "," + this.paramB + "," + this.block;
   }
}
