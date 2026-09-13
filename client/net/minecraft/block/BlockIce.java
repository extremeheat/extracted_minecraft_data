package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIce extends BlockBreakable {
   public BlockIce() {
      super("ice", Material.field_151588_w, false);
      this.field_149765_K = 0.98F;
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public int func_149701_w() {
      return 1;
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return super.func_149646_a(var1, var2, var3, var4, 1 - var5);
   }

   @Override
   public void func_149636_a(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
      var2.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
      var2.func_71020_j(0.025F);
      if (this.func_149700_E() && EnchantmentHelper.func_77502_d(var2)) {
         ItemStack var9 = this.func_149644_j(var6);
         if (var9 != null) {
            this.func_149642_a(var1, var3, var4, var5, var9);
         }
      } else {
         if (var1.field_73011_w.field_76575_d) {
            var1.func_147468_f(var3, var4, var5);
            return;
         }

         int var7 = EnchantmentHelper.func_77517_e(var2);
         this.func_149697_b(var1, var3, var4, var5, var6, var7);
         Material var8 = var1.func_147439_a(var3, var4 - 1, var5).func_149688_o();
         if (var8.func_76230_c() || var8.func_76224_d()) {
            var1.func_147449_b(var3, var4, var5, Blocks.field_150358_i);
         }
      }
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (var1.func_72972_b(EnumSkyBlock.Block, var2, var3, var4) > 11 - this.func_149717_k()) {
         if (var1.field_73011_w.field_76575_d) {
            var1.func_147468_f(var2, var3, var4);
            return;
         }

         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147449_b(var2, var3, var4, Blocks.field_150355_j);
      }
   }

   @Override
   public int func_149656_h() {
      return 0;
   }
}
