package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStainedGlass extends BlockBreakable {
   private static final IIcon[] field_149998_a = new IIcon[16];

   public BlockStainedGlass(Material var1) {
      super("glass", var1, false);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return field_149998_a[var2 % field_149998_a.length];
   }

   @Override
   public int func_149692_a(int var1) {
      return var1;
   }

   public static int func_149997_b(int var0) {
      return ~var0 & 15;
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      for(int var4 = 0; var4 < field_149998_a.length; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }
   }

   @Override
   public int func_149701_w() {
      return 1;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      for(int var2 = 0; var2 < field_149998_a.length; ++var2) {
         field_149998_a[var2] = var1.func_94245_a(this.func_149641_N() + "_" + ItemDye.field_150921_b[func_149997_b(var2)]);
      }
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   @Override
   protected boolean func_149700_E() {
      return true;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }
}
