package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

public class BlockSeaLantern extends Block {
   public BlockSeaLantern(Material var1) {
      super(var1);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public int func_149745_a(Random var1) {
      return 2 + var1.nextInt(2);
   }

   public int func_149679_a(int var1, Random var2) {
      return MathHelper.func_76125_a(this.func_149745_a(var2) + var2.nextInt(var1 + 1), 1, 5);
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_179563_cD;
   }

   public MapColor func_180659_g(IBlockState var1) {
      return MapColor.field_151677_p;
   }

   protected boolean func_149700_E() {
      return true;
   }
}
