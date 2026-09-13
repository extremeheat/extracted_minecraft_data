package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

public class BlockBookshelf extends Block {
   public BlockBookshelf() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return var1 != 1 && var1 != 0 ? super.func_149691_a(var1, var2) : Blocks.field_150344_f.func_149733_h(var1);
   }

   @Override
   public int func_149745_a(Random var1) {
      return 3;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151122_aG;
   }
}
