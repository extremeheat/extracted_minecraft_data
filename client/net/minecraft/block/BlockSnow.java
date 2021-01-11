package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSnow extends Block {
   public static final PropertyInteger field_176315_a = PropertyInteger.func_177719_a("layers", 1, 8);

   protected BlockSnow() {
      super(Material.field_151597_y);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176315_a, 1));
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.func_149683_g();
   }

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return (Integer)var1.func_180495_p(var2).func_177229_b(field_176315_a) < 5;
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      int var4 = (Integer)var3.func_177229_b(field_176315_a) - 1;
      float var5 = 0.125F;
      return new AxisAlignedBB((double)var2.func_177958_n() + this.field_149759_B, (double)var2.func_177956_o() + this.field_149760_C, (double)var2.func_177952_p() + this.field_149754_D, (double)var2.func_177958_n() + this.field_149755_E, (double)((float)var2.func_177956_o() + (float)var4 * var5), (double)var2.func_177952_p() + this.field_149757_G);
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public void func_149683_g() {
      this.func_150154_b(0);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      this.func_150154_b((Integer)var3.func_177229_b(field_176315_a));
   }

   protected void func_150154_b(int var1) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, (float)var1 / 8.0F, 1.0F);
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2.func_177977_b());
      Block var4 = var3.func_177230_c();
      if (var4 != Blocks.field_150432_aD && var4 != Blocks.field_150403_cj) {
         if (var4.func_149688_o() == Material.field_151584_j) {
            return true;
         } else if (var4 == this && (Integer)var3.func_177229_b(field_176315_a) >= 7) {
            return true;
         } else {
            return var4.func_149662_c() && var4.field_149764_J.func_76230_c();
         }
      } else {
         return false;
      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.func_176314_e(var1, var2, var3);
   }

   private boolean func_176314_e(World var1, BlockPos var2, IBlockState var3) {
      if (!this.func_176196_c(var1, var2)) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
         return false;
      } else {
         return true;
      }
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      func_180635_a(var1, var3, new ItemStack(Items.field_151126_ay, (Integer)var4.func_177229_b(field_176315_a) + 1, 0));
      var1.func_175698_g(var3);
      var2.func_71029_a(StatList.field_75934_C[Block.func_149682_b(this)]);
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151126_ay;
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var1.func_175642_b(EnumSkyBlock.BLOCK, var2) > 11) {
         this.func_176226_b(var1, var2, var1.func_180495_p(var2), 0);
         var1.func_175698_g(var2);
      }

   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var3 == EnumFacing.UP ? true : super.func_176225_a(var1, var2, var3);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176315_a, (var1 & 7) + 1);
   }

   public boolean func_176200_f(World var1, BlockPos var2) {
      return (Integer)var1.func_180495_p(var2).func_177229_b(field_176315_a) == 1;
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176315_a) - 1;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176315_a});
   }
}
