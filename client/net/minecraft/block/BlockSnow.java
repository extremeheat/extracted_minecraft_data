package net.minecraft.block;

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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSnow extends Block {
   protected BlockSnow() {
      super(Material.field_151597_y);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.func_150154_b(0);
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("snow");
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4) & 7;
      float var6 = 0.125F;
      return AxisAlignedBB.func_72330_a(
         (double)var2 + this.field_149759_B,
         (double)var3 + this.field_149760_C,
         (double)var4 + this.field_149754_D,
         (double)var2 + this.field_149755_E,
         (double)((float)var3 + (float)var5 * var6),
         (double)var4 + this.field_149757_G
      );
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public void func_149683_g() {
      this.func_150154_b(0);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.func_150154_b(var1.func_72805_g(var2, var3, var4));
   }

   protected void func_150154_b(int var1) {
      int var2 = var1 & 7;
      float var3 = (float)(2 * (1 + var2)) / 16.0F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, var3, 1.0F);
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      Block var5 = var1.func_147439_a(var2, var3 - 1, var4);
      if (var5 != Blocks.field_150432_aD && var5 != Blocks.field_150403_cj) {
         if (var5.func_149688_o() == Material.field_151584_j) {
            return true;
         } else if (var5 == this && (var1.func_72805_g(var2, var3 - 1, var4) & 7) == 7) {
            return true;
         } else {
            return var5.func_149662_c() && var5.field_149764_J.func_76230_c();
         }
      } else {
         return false;
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      this.func_150155_m(var1, var2, var3, var4);
   }

   private boolean func_150155_m(World var1, int var2, int var3, int var4) {
      if (!this.func_149742_c(var1, var2, var3, var4)) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void func_149636_a(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
      int var7 = var6 & 7;
      this.func_149642_a(var1, var3, var4, var5, new ItemStack(Items.field_151126_ay, var7 + 1, 0));
      var1.func_147468_f(var3, var4, var5);
      var2.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151126_ay;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (var1.func_72972_b(EnumSkyBlock.Block, var2, var3, var4) > 11) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
      }
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var5 == 1 ? true : super.func_149646_a(var1, var2, var3, var4, var5);
   }
}
