package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockBush extends Block {
   protected BlockBush() {
      this(Material.field_151585_k);
   }

   protected BlockBush(Material var1) {
      this(var1, var1.func_151565_r());
   }

   protected BlockBush(Material var1, MapColor var2) {
      super(var1, var2);
      this.func_149675_a(true);
      float var3 = 0.2F;
      this.func_149676_a(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var3 * 3.0F, 0.5F + var3);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return super.func_176196_c(var1, var2) && this.func_149854_a(var1.func_180495_p(var2.func_177977_b()).func_177230_c());
   }

   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150349_c || var1 == Blocks.field_150346_d || var1 == Blocks.field_150458_ak;
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      super.func_176204_a(var1, var2, var3, var4);
      this.func_176475_e(var1, var2, var3);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      this.func_176475_e(var1, var2, var3);
   }

   protected void func_176475_e(World var1, BlockPos var2, IBlockState var3) {
      if (!this.func_180671_f(var1, var2, var3)) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 3);
      }

   }

   public boolean func_180671_f(World var1, BlockPos var2, IBlockState var3) {
      return this.func_149854_a(var1.func_180495_p(var2.func_177977_b()).func_177230_c());
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }
}
