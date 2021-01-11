package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockIce extends BlockBreakable {
   public BlockIce() {
      super(Material.field_151588_w, false);
      this.field_149765_K = 0.98F;
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      var2.func_71029_a(StatList.field_75934_C[Block.func_149682_b(this)]);
      var2.func_71020_j(0.025F);
      if (this.func_149700_E() && EnchantmentHelper.func_77502_d(var2)) {
         ItemStack var8 = this.func_180643_i(var4);
         if (var8 != null) {
            func_180635_a(var1, var3, var8);
         }
      } else {
         if (var1.field_73011_w.func_177500_n()) {
            var1.func_175698_g(var3);
            return;
         }

         int var6 = EnchantmentHelper.func_77517_e(var2);
         this.func_176226_b(var1, var3, var4, var6);
         Material var7 = var1.func_180495_p(var3.func_177977_b()).func_177230_c().func_149688_o();
         if (var7.func_76230_c() || var7.func_76224_d()) {
            var1.func_175656_a(var3, Blocks.field_150358_i.func_176223_P());
         }
      }

   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var1.func_175642_b(EnumSkyBlock.BLOCK, var2) > 11 - this.func_149717_k()) {
         if (var1.field_73011_w.func_177500_n()) {
            var1.func_175698_g(var2);
         } else {
            this.func_176226_b(var1, var2, var1.func_180495_p(var2), 0);
            var1.func_175656_a(var2, Blocks.field_150355_j.func_176223_P());
         }
      }
   }

   public int func_149656_h() {
      return 0;
   }
}
