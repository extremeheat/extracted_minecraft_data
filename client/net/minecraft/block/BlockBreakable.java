package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockBreakable extends Block {
   private boolean field_149996_a;

   protected BlockBreakable(Material var1, boolean var2) {
      this(var1, var2, var1.func_151565_r());
   }

   protected BlockBreakable(Material var1, boolean var2, MapColor var3) {
      super(var1, var3);
      this.field_149996_a = var2;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      Block var5 = var4.func_177230_c();
      if (this == Blocks.field_150359_w || this == Blocks.field_150399_cn) {
         if (var1.func_180495_p(var2.func_177972_a(var3.func_176734_d())) != var4) {
            return true;
         }

         if (var5 == this) {
            return false;
         }
      }

      return !this.field_149996_a && var5 == this ? false : super.func_176225_a(var1, var2, var3);
   }
}
