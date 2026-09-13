package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStainedGlassPane extends BlockPane {
   private static final IIcon[] field_150106_a = new IIcon[16];
   private static final IIcon[] field_150105_b = new IIcon[16];

   public BlockStainedGlassPane() {
      super("glass", "glass_pane_top", Material.field_151592_s, false);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public IIcon func_149735_b(int var1, int var2) {
      return field_150106_a[var2 % field_150106_a.length];
   }

   public IIcon func_150104_b(int var1) {
      return field_150105_b[~var1 & 15];
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return this.func_149735_b(var1, ~var2 & 15);
   }

   @Override
   public int func_149692_a(int var1) {
      return var1;
   }

   public static int func_150103_c(int var0) {
      return var0 & 15;
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      for(int var4 = 0; var4 < field_150106_a.length; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }
   }

   @Override
   public int func_149701_w() {
      return 1;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      super.func_149651_a(var1);

      for(int var2 = 0; var2 < field_150106_a.length; ++var2) {
         field_150106_a[var2] = var1.func_94245_a(this.func_149641_N() + "_" + ItemDye.field_150921_b[func_150103_c(var2)]);
         field_150105_b[var2] = var1.func_94245_a(this.func_149641_N() + "_pane_top_" + ItemDye.field_150921_b[func_150103_c(var2)]);
      }
   }
}
