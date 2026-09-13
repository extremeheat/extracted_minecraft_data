package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockHardenedClay extends Block {
   public BlockHardenedClay() {
      super(Material.field_151576_e);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public MapColor func_149728_f(int var1) {
      return MapColor.field_151676_q;
   }
}
