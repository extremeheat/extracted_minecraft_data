package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockWoodSlab extends BlockSlab {
   public static final String[] field_150005_b = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "big_oak"};

   public BlockWoodSlab(boolean var1) {
      super(var1, Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return Blocks.field_150344_f.func_149691_a(var1, var2 & 7);
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150376_bx);
   }

   @Override
   protected ItemStack func_149644_j(int var1) {
      return new ItemStack(Item.func_150898_a(Blocks.field_150376_bx), 2, var1 & 7);
   }

   @Override
   public String func_150002_b(int var1) {
      if (var1 < 0 || var1 >= field_150005_b.length) {
         var1 = 0;
      }

      return super.func_149739_a() + "." + field_150005_b[var1];
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      if (var1 != Item.func_150898_a(Blocks.field_150373_bw)) {
         for(int var4 = 0; var4 < field_150005_b.length; ++var4) {
            var3.add(new ItemStack(var1, 1, var4));
         }
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
   }
}
