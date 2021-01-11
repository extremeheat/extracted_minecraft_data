package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
   protected BlockBasePressurePlate(Material var1) {
      this(var1, var1.func_151565_r());
   }

   protected BlockBasePressurePlate(Material var1, MapColor var2) {
      super(var1, var2);
      this.func_149647_a(CreativeTabs.field_78028_d);
      this.func_149675_a(true);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      this.func_180668_d(var1.func_180495_p(var2));
   }

   protected void func_180668_d(IBlockState var1) {
      boolean var2 = this.func_176576_e(var1) > 0;
      float var3 = 0.0625F;
      if (var2) {
         this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
      } else {
         this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
      }

   }

   public int func_149738_a(World var1) {
      return 20;
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

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return true;
   }

   public boolean func_181623_g() {
      return true;
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return this.func_176577_m(var1, var2.func_177977_b());
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.func_176577_m(var1, var2.func_177977_b())) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
      }

   }

   private boolean func_176577_m(World var1, BlockPos var2) {
      return World.func_175683_a(var1, var2) || var1.func_180495_p(var2).func_177230_c() instanceof BlockFence;
   }

   public void func_180645_a(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         int var5 = this.func_176576_e(var3);
         if (var5 > 0) {
            this.func_180666_a(var1, var2, var3, var5);
         }

      }
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (!var1.field_72995_K) {
         int var5 = this.func_176576_e(var3);
         if (var5 == 0) {
            this.func_180666_a(var1, var2, var3, var5);
         }

      }
   }

   protected void func_180666_a(World var1, BlockPos var2, IBlockState var3, int var4) {
      int var5 = this.func_180669_e(var1, var2);
      boolean var6 = var4 > 0;
      boolean var7 = var5 > 0;
      if (var4 != var5) {
         var3 = this.func_176575_a(var3, var5);
         var1.func_180501_a(var2, var3, 2);
         this.func_176578_d(var1, var2);
         var1.func_175704_b(var2, var2);
      }

      if (!var7 && var6) {
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.1D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.3F, 0.5F);
      } else if (var7 && !var6) {
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.1D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.3F, 0.6F);
      }

      if (var7) {
         var1.func_175684_a(var2, this, this.func_149738_a(var1));
      }

   }

   protected AxisAlignedBB func_180667_a(BlockPos var1) {
      float var2 = 0.125F;
      return new AxisAlignedBB((double)((float)var1.func_177958_n() + 0.125F), (double)var1.func_177956_o(), (double)((float)var1.func_177952_p() + 0.125F), (double)((float)(var1.func_177958_n() + 1) - 0.125F), (double)var1.func_177956_o() + 0.25D, (double)((float)(var1.func_177952_p() + 1) - 0.125F));
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      if (this.func_176576_e(var3) > 0) {
         this.func_176578_d(var1, var2);
      }

      super.func_180663_b(var1, var2, var3);
   }

   protected void func_176578_d(World var1, BlockPos var2) {
      var1.func_175685_c(var2, this);
      var1.func_175685_c(var2.func_177977_b(), this);
   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return this.func_176576_e(var3);
   }

   public int func_176211_b(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return var4 == EnumFacing.UP ? this.func_176576_e(var3) : 0;
   }

   public boolean func_149744_f() {
      return true;
   }

   public void func_149683_g() {
      float var1 = 0.5F;
      float var2 = 0.125F;
      float var3 = 0.5F;
      this.func_149676_a(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
   }

   public int func_149656_h() {
      return 1;
   }

   protected abstract int func_180669_e(World var1, BlockPos var2);

   protected abstract int func_176576_e(IBlockState var1);

   protected abstract IBlockState func_176575_a(IBlockState var1, int var2);
}
