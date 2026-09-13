package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;

public class BlockCompressedPowered extends BlockCompressed {
   public BlockCompressedPowered(MapColor var1) {
      super(var1);
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   @Override
   public boolean func_149744_f() {
      return true;
   }

   @Override
   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return 15;
   }
}
