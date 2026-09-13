package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

public class BlockMelon extends Block {
   private IIcon field_150201_a;

   protected BlockMelon() {
      super(Material.field_151572_C);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return var1 != 1 && var1 != 0 ? this.field_149761_L : this.field_150201_a;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151127_ba;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 3 + var1.nextInt(5);
   }

   @Override
   public int func_149679_a(int var1, Random var2) {
      int var3 = this.func_149745_a(var2) + var2.nextInt(1 + var1);
      if (var3 > 9) {
         var3 = 9;
      }

      return var3;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
      this.field_150201_a = var1.func_94245_a(this.func_149641_N() + "_top");
   }
}
