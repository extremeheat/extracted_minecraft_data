package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockBookshelf extends Block {
   public BlockBookshelf() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public int func_149745_a(Random var1) {
      return 3;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151122_aG;
   }
}
