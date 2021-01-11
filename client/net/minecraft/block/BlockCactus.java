package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockCactus extends Block {
   public static final PropertyInteger field_176587_a = PropertyInteger.func_177719_a("age", 0, 15);

   protected BlockCactus() {
      super(Material.field_151570_A);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176587_a, 0));
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      BlockPos var5 = var2.func_177984_a();
      if (var1.func_175623_d(var5)) {
         int var6;
         for(var6 = 1; var1.func_180495_p(var2.func_177979_c(var6)).func_177230_c() == this; ++var6) {
         }

         if (var6 < 3) {
            int var7 = (Integer)var3.func_177229_b(field_176587_a);
            if (var7 == 15) {
               var1.func_175656_a(var5, this.func_176223_P());
               IBlockState var8 = var3.func_177226_a(field_176587_a, 0);
               var1.func_180501_a(var2, var8, 4);
               this.func_176204_a(var1, var5, var8, this);
            } else {
               var1.func_180501_a(var2, var3.func_177226_a(field_176587_a, var7 + 1), 4);
            }

         }
      }
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      float var4 = 0.0625F;
      return new AxisAlignedBB((double)((float)var2.func_177958_n() + var4), (double)var2.func_177956_o(), (double)((float)var2.func_177952_p() + var4), (double)((float)(var2.func_177958_n() + 1) - var4), (double)((float)(var2.func_177956_o() + 1) - var4), (double)((float)(var2.func_177952_p() + 1) - var4));
   }

   public AxisAlignedBB func_180646_a(World var1, BlockPos var2) {
      float var3 = 0.0625F;
      return new AxisAlignedBB((double)((float)var2.func_177958_n() + var3), (double)var2.func_177956_o(), (double)((float)var2.func_177952_p() + var3), (double)((float)(var2.func_177958_n() + 1) - var3), (double)(var2.func_177956_o() + 1), (double)((float)(var2.func_177952_p() + 1) - var3));
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return super.func_176196_c(var1, var2) ? this.func_176586_d(var1, var2) : false;
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.func_176586_d(var1, var2)) {
         var1.func_175655_b(var2, true);
      }

   }

   public boolean func_176586_d(World var1, BlockPos var2) {
      Iterator var3 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var3.hasNext()) {
         EnumFacing var4 = (EnumFacing)var3.next();
         if (var1.func_180495_p(var2.func_177972_a(var4)).func_177230_c().func_149688_o().func_76220_a()) {
            return false;
         }
      }

      Block var5 = var1.func_180495_p(var2.func_177977_b()).func_177230_c();
      return var5 == Blocks.field_150434_aF || var5 == Blocks.field_150354_m;
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      var4.func_70097_a(DamageSource.field_76367_g, 1.0F);
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176587_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176587_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176587_a});
   }
}
