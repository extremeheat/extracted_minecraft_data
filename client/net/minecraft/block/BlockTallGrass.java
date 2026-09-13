package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTallGrass extends BlockBush implements IGrowable {
   private static final String[] field_149871_a = new String[]{"deadbush", "tallgrass", "fern"};
   private IIcon[] field_149870_b;

   protected BlockTallGrass() {
      super(Material.field_151582_l);
      float var1 = 0.4F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.8F, 0.5F + var1);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var2 >= this.field_149870_b.length) {
         var2 = 0;
      }

      return this.field_149870_b[var2];
   }

   @Override
   public int func_149635_D() {
      double var1 = 0.5;
      double var3 = 1.0;
      return ColorizerGrass.func_77480_a(var1, var3);
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      return this.func_149854_a(var1.func_147439_a(var2, var3 - 1, var4));
   }

   @Override
   public int func_149741_i(int var1) {
      return var1 == 0 ? 16777215 : ColorizerGrass.func_77480_a(0.5, 1.0);
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      return var5 == 0 ? 16777215 : var1.func_72807_a(var2, var4).func_150558_b(var2, var3, var4);
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return var2.nextInt(8) == 0 ? Items.field_151014_N : null;
   }

   @Override
   public int func_149679_a(int var1, Random var2) {
      return 1 + var2.nextInt(var1 * 2 + 1);
   }

   @Override
   public void func_149636_a(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
      if (!var1.field_72995_K && var2.func_71045_bC() != null && var2.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
         var2.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
         this.func_149642_a(var1, var3, var4, var5, new ItemStack(Blocks.field_150329_H, 1, var6));
      } else {
         super.func_149636_a(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public int func_149643_k(World var1, int var2, int var3, int var4) {
      return var1.func_72805_g(var2, var3, var4);
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      for(int var4 = 1; var4 < 3; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149870_b = new IIcon[field_149871_a.length];

      for(int var2 = 0; var2 < this.field_149870_b.length; ++var2) {
         this.field_149870_b[var2] = var1.func_94245_a(field_149871_a[var2]);
      }
   }

   @Override
   public boolean func_149851_a(World var1, int var2, int var3, int var4, boolean var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      return var6 != 0;
   }

   @Override
   public boolean func_149852_a(World var1, Random var2, int var3, int var4, int var5) {
      return true;
   }

   @Override
   public void func_149853_b(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = var1.func_72805_g(var3, var4, var5);
      byte var7 = 2;
      if (var6 == 2) {
         var7 = 3;
      }

      if (Blocks.field_150398_cm.func_149742_c(var1, var3, var4, var5)) {
         Blocks.field_150398_cm.func_149889_c(var1, var3, var4, var5, var7, 2);
      }
   }
}
