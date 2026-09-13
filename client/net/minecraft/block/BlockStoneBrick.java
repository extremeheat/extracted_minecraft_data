package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStoneBrick extends Block {
   public static final String[] field_150142_a = new String[]{"default", "mossy", "cracked", "chiseled"};
   public static final String[] field_150141_b = new String[]{null, "mossy", "cracked", "carved"};
   private IIcon[] field_150143_M;

   public BlockStoneBrick() {
      super(Material.field_151576_e);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var2 < 0 || var2 >= field_150141_b.length) {
         var2 = 0;
      }

      return this.field_150143_M[var2];
   }

   @Override
   public int func_149692_a(int var1) {
      return var1;
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      for(int var4 = 0; var4 < 4; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_150143_M = new IIcon[field_150141_b.length];

      for(int var2 = 0; var2 < this.field_150143_M.length; ++var2) {
         String var3 = this.func_149641_N();
         if (field_150141_b[var2] != null) {
            var3 = var3 + "_" + field_150141_b[var2];
         }

         this.field_150143_M[var2] = var1.func_94245_a(var3);
      }
   }
}
