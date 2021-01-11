package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCocoa extends BlockDirectional implements IGrowable {
   public static final PropertyInteger field_176501_a = PropertyInteger.func_177719_a("age", 0, 2);

   public BlockCocoa() {
      super(Material.field_151585_k);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176387_N, EnumFacing.NORTH).func_177226_a(field_176501_a, 0));
      this.func_149675_a(true);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!this.func_176499_e(var1, var2, var3)) {
         this.func_176500_f(var1, var2, var3);
      } else if (var1.field_73012_v.nextInt(5) == 0) {
         int var5 = (Integer)var3.func_177229_b(field_176501_a);
         if (var5 < 2) {
            var1.func_180501_a(var2, var3.func_177226_a(field_176501_a, var5 + 1), 2);
         }
      }

   }

   public boolean func_176499_e(World var1, BlockPos var2, IBlockState var3) {
      var2 = var2.func_177972_a((EnumFacing)var3.func_177229_b(field_176387_N));
      IBlockState var4 = var1.func_180495_p(var2);
      return var4.func_177230_c() == Blocks.field_150364_r && var4.func_177229_b(BlockPlanks.field_176383_a) == BlockPlanks.EnumType.JUNGLE;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_149662_c() {
      return false;
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      this.func_180654_a(var1, var2);
      return super.func_180640_a(var1, var2, var3);
   }

   public AxisAlignedBB func_180646_a(World var1, BlockPos var2) {
      this.func_180654_a(var1, var2);
      return super.func_180646_a(var1, var2);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176387_N);
      int var5 = (Integer)var3.func_177229_b(field_176501_a);
      int var6 = 4 + var5 * 2;
      int var7 = 5 + var5 * 2;
      float var8 = (float)var6 / 2.0F;
      switch(var4) {
      case SOUTH:
         this.func_149676_a((8.0F - var8) / 16.0F, (12.0F - (float)var7) / 16.0F, (15.0F - (float)var6) / 16.0F, (8.0F + var8) / 16.0F, 0.75F, 0.9375F);
         break;
      case NORTH:
         this.func_149676_a((8.0F - var8) / 16.0F, (12.0F - (float)var7) / 16.0F, 0.0625F, (8.0F + var8) / 16.0F, 0.75F, (1.0F + (float)var6) / 16.0F);
         break;
      case WEST:
         this.func_149676_a(0.0625F, (12.0F - (float)var7) / 16.0F, (8.0F - var8) / 16.0F, (1.0F + (float)var6) / 16.0F, 0.75F, (8.0F + var8) / 16.0F);
         break;
      case EAST:
         this.func_149676_a((15.0F - (float)var6) / 16.0F, (12.0F - (float)var7) / 16.0F, (8.0F - var8) / 16.0F, 0.9375F, 0.75F, (8.0F + var8) / 16.0F);
      }

   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      EnumFacing var6 = EnumFacing.func_176733_a((double)var4.field_70177_z);
      var1.func_180501_a(var2, var3.func_177226_a(field_176387_N, var6), 2);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      if (!var3.func_176740_k().func_176722_c()) {
         var3 = EnumFacing.NORTH;
      }

      return this.func_176223_P().func_177226_a(field_176387_N, var3.func_176734_d()).func_177226_a(field_176501_a, 0);
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.func_176499_e(var1, var2, var3)) {
         this.func_176500_f(var1, var2, var3);
      }

   }

   private void func_176500_f(World var1, BlockPos var2, IBlockState var3) {
      var1.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 3);
      this.func_176226_b(var1, var2, var3, 0);
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      int var6 = (Integer)var3.func_177229_b(field_176501_a);
      byte var7 = 1;
      if (var6 >= 2) {
         var7 = 3;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         func_180635_a(var1, var2, new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BROWN.func_176767_b()));
      }

   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151100_aR;
   }

   public int func_176222_j(World var1, BlockPos var2) {
      return EnumDyeColor.BROWN.func_176767_b();
   }

   public boolean func_176473_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return (Integer)var3.func_177229_b(field_176501_a) < 2;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      var1.func_180501_a(var3, var4.func_177226_a(field_176501_a, (Integer)var4.func_177229_b(field_176501_a) + 1), 2);
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176387_N, EnumFacing.func_176731_b(var1)).func_177226_a(field_176501_a, (var1 & 15) >> 2);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176387_N)).func_176736_b();
      var3 |= (Integer)var1.func_177229_b(field_176501_a) << 2;
      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176387_N, field_176501_a});
   }
}
