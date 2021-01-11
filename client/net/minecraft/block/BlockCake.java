package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCake extends Block {
   public static final PropertyInteger field_176589_a = PropertyInteger.func_177719_a("bites", 0, 6);

   protected BlockCake() {
      super(Material.field_151568_F);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176589_a, 0));
      this.func_149675_a(true);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      float var3 = 0.0625F;
      float var4 = (float)(1 + (Integer)var1.func_180495_p(var2).func_177229_b(field_176589_a) * 2) / 16.0F;
      float var5 = 0.5F;
      this.func_149676_a(var4, 0.0F, var3, 1.0F - var3, var5, 1.0F - var3);
   }

   public void func_149683_g() {
      float var1 = 0.0625F;
      float var2 = 0.5F;
      this.func_149676_a(var1, 0.0F, var1, 1.0F - var1, var2, 1.0F - var1);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      float var4 = 0.0625F;
      float var5 = (float)(1 + (Integer)var3.func_177229_b(field_176589_a) * 2) / 16.0F;
      float var6 = 0.5F;
      return new AxisAlignedBB((double)((float)var2.func_177958_n() + var5), (double)var2.func_177956_o(), (double)((float)var2.func_177952_p() + var4), (double)((float)(var2.func_177958_n() + 1) - var4), (double)((float)var2.func_177956_o() + var6), (double)((float)(var2.func_177952_p() + 1) - var4));
   }

   public AxisAlignedBB func_180646_a(World var1, BlockPos var2) {
      return this.func_180640_a(var1, var2, var1.func_180495_p(var2));
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      this.func_180682_b(var1, var2, var3, var4);
      return true;
   }

   public void func_180649_a(World var1, BlockPos var2, EntityPlayer var3) {
      this.func_180682_b(var1, var2, var1.func_180495_p(var2), var3);
   }

   private void func_180682_b(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (var4.func_71043_e(false)) {
         var4.func_71029_a(StatList.field_181724_H);
         var4.func_71024_bL().func_75122_a(2, 0.1F);
         int var5 = (Integer)var3.func_177229_b(field_176589_a);
         if (var5 < 6) {
            var1.func_180501_a(var2, var3.func_177226_a(field_176589_a, var5 + 1), 3);
         } else {
            var1.func_175698_g(var2);
         }

      }
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return super.func_176196_c(var1, var2) ? this.func_176588_d(var1, var2) : false;
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.func_176588_d(var1, var2)) {
         var1.func_175698_g(var2);
      }

   }

   private boolean func_176588_d(World var1, BlockPos var2) {
      return var1.func_180495_p(var2.func_177977_b()).func_177230_c().func_149688_o().func_76220_a();
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return null;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151105_aU;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176589_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176589_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176589_a});
   }

   public int func_180641_l(World var1, BlockPos var2) {
      return (7 - (Integer)var1.func_180495_p(var2).func_177229_b(field_176589_a)) * 2;
   }

   public boolean func_149740_M() {
      return true;
   }
}
