package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockLeavesBase extends Block {
   protected boolean field_150121_P;

   protected BlockLeavesBase(Material var1, boolean var2) {
      super(var1);
      this.field_150121_P = var2;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return !this.field_150121_P && var1.func_180495_p(var2).func_177230_c() == this ? false : super.func_176225_a(var1, var2, var3);
   }
}
