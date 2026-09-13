package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDirt extends Block {
   public static final String[] field_150009_a = new String[]{"default", "default", "podzol"};
   private IIcon field_150008_b;
   private IIcon field_150010_M;

   protected BlockDirt() {
      super(Material.field_151578_c);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var2 == 2) {
         if (var1 == 1) {
            return this.field_150008_b;
         }

         if (var1 != 0) {
            return this.field_150010_M;
         }
      }

      return this.field_149761_L;
   }

   @Override
   public IIcon func_149673_e(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      if (var6 == 2) {
         if (var5 == 1) {
            return this.field_150008_b;
         }

         if (var5 != 0) {
            Material var7 = var1.func_147439_a(var2, var3 + 1, var4).func_149688_o();
            if (var7 == Material.field_151597_y || var7 == Material.field_151596_z) {
               return Blocks.field_150349_c.func_149673_e(var1, var2, var3, var4, var5);
            }

            Block var8 = var1.func_147439_a(var2, var3 + 1, var4);
            if (var8 != Blocks.field_150346_d && var8 != Blocks.field_150349_c) {
               return this.field_150010_M;
            }
         }
      }

      return this.field_149761_L;
   }

   @Override
   public int func_149692_a(int var1) {
      return 0;
   }

   @Override
   protected ItemStack func_149644_j(int var1) {
      if (var1 == 1) {
         var1 = 0;
      }

      return super.func_149644_j(var1);
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(this, 1, 0));
      var3.add(new ItemStack(this, 1, 2));
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      super.func_149651_a(var1);
      this.field_150008_b = var1.func_94245_a(this.func_149641_N() + "_" + "podzol_top");
      this.field_150010_M = var1.func_94245_a(this.func_149641_N() + "_" + "podzol_side");
   }

   @Override
   public int func_149643_k(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      if (var5 == 1) {
         var5 = 0;
      }

      return var5;
   }
}
