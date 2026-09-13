package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockWood extends Block {
   public static final String[] field_150096_a = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "big_oak"};
   private IIcon[] field_150095_b;

   public BlockWood() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var2 < 0 || var2 >= this.field_150095_b.length) {
         var2 = 0;
      }

      return this.field_150095_b[var2];
   }

   @Override
   public int func_149692_a(int var1) {
      return var1;
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
      var3.add(new ItemStack(var1, 1, 2));
      var3.add(new ItemStack(var1, 1, 3));
      var3.add(new ItemStack(var1, 1, 4));
      var3.add(new ItemStack(var1, 1, 5));
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_150095_b = new IIcon[field_150096_a.length];

      for(int var2 = 0; var2 < this.field_150095_b.length; ++var2) {
         this.field_150095_b[var2] = var1.func_94245_a(this.func_149641_N() + "_" + field_150096_a[var2]);
      }
   }
}
