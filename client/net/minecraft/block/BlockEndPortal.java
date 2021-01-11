package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEndPortal extends BlockContainer {
   protected BlockEndPortal(Material var1) {
      super(var1);
      this.func_149715_a(1.0F);
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityEndPortal();
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      float var3 = 0.0625F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, var3, 1.0F);
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var3 == EnumFacing.DOWN ? super.func_176225_a(var1, var2, var3) : false;
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (var4.field_70154_o == null && var4.field_70153_n == null && !var1.field_72995_K) {
         var4.func_71027_c(1);
      }

   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      double var5 = (double)((float)var2.func_177958_n() + var4.nextFloat());
      double var7 = (double)((float)var2.func_177956_o() + 0.8F);
      double var9 = (double)((float)var2.func_177952_p() + var4.nextFloat());
      double var11 = 0.0D;
      double var13 = 0.0D;
      double var15 = 0.0D;
      var1.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var5, var7, var9, var11, var13, var15);
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return null;
   }

   public MapColor func_180659_g(IBlockState var1) {
      return MapColor.field_151646_E;
   }
}
