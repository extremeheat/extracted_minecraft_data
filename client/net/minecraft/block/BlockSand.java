package net.minecraft.block;

import net.minecraft.block.state.IBlockState;

public class BlockSand extends BlockFalling {
   private final int field_196445_a;

   public BlockSand(int var1, Block.Properties var2) {
      super(var2);
      this.field_196445_a = var1;
   }

   public int func_189876_x(IBlockState var1) {
      return this.field_196445_a;
   }
}
