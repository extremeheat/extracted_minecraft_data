package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumWorldBlockLayer;

public class BlockGlass extends BlockBreakable {
   public BlockGlass(Material var1, boolean var2) {
      super(var1, var2);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public boolean func_149686_d() {
      return false;
   }

   protected boolean func_149700_E() {
      return true;
   }
}
