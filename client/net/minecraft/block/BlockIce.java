package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockIce extends BlockBreakable {
   public BlockIce(Block.Properties var1) {
      super(var1);
   }

   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return Blocks.field_150355_j.func_176223_P().func_200016_a(var2, var3);
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      var2.func_71029_a(StatList.field_188065_ae.func_199076_b(this));
      var2.func_71020_j(0.005F);
      if (this.func_149700_E() && EnchantmentHelper.func_77506_a(Enchantments.field_185306_r, var6) > 0) {
         func_180635_a(var1, var3, this.func_180643_i(var4));
      } else {
         if (var1.field_73011_w.func_177500_n()) {
            var1.func_175698_g(var3);
            return;
         }

         int var7 = EnchantmentHelper.func_77506_a(Enchantments.field_185308_t, var6);
         var4.func_196949_c(var1, var3, var7);
         Material var8 = var1.func_180495_p(var3.func_177977_b()).func_185904_a();
         if (var8.func_76230_c() || var8.func_76224_d()) {
            var1.func_175656_a(var3, Blocks.field_150355_j.func_176223_P());
         }
      }

   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var2.func_175642_b(EnumLightType.BLOCK, var3) > 11 - var1.func_200016_a(var2, var3)) {
         this.func_196454_d(var1, var2, var3);
      }

   }

   protected void func_196454_d(IBlockState var1, World var2, BlockPos var3) {
      if (var2.field_73011_w.func_177500_n()) {
         var2.func_175698_g(var3);
      } else {
         var1.func_196949_c(var2, var3, 0);
         var2.func_175656_a(var3, Blocks.field_150355_j.func_176223_P());
         var2.func_190524_a(var3, Blocks.field_150355_j, var3);
      }
   }

   public EnumPushReaction func_149656_h(IBlockState var1) {
      return EnumPushReaction.NORMAL;
   }
}
