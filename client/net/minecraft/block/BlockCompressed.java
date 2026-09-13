package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockCompressed extends Block {
   private final MapColor field_150202_a;

   public BlockCompressed(MapColor var1) {
      super(Material.field_151573_f);
      this.field_150202_a = var1;
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public MapColor func_149728_f(int var1) {
      return this.field_150202_a;
   }
}
