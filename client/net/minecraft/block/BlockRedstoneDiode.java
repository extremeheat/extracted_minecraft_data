package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockDirectional {
   protected final boolean field_149914_a;

   protected BlockRedstoneDiode(boolean var1) {
      super(Material.field_151594_q);
      this.field_149914_a = var1;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return World.func_175683_a(var1, var2.func_177977_b()) ? super.func_176196_c(var1, var2) : false;
   }

   public boolean func_176409_d(World var1, BlockPos var2) {
      return World.func_175683_a(var1, var2.func_177977_b());
   }

   public void func_180645_a(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!this.func_176405_b(var1, var2, var3)) {
         boolean var5 = this.func_176404_e(var1, var2, var3);
         if (this.field_149914_a && !var5) {
            var1.func_180501_a(var2, this.func_180675_k(var3), 2);
         } else if (!this.field_149914_a) {
            var1.func_180501_a(var2, this.func_180674_e(var3), 2);
            if (!var5) {
               var1.func_175654_a(var2, this.func_180674_e(var3).func_177230_c(), this.func_176399_m(var3), -1);
            }
         }

      }
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var3.func_176740_k() != EnumFacing.Axis.Y;
   }

   protected boolean func_176406_l(IBlockState var1) {
      return this.field_149914_a;
   }

   public int func_176211_b(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return this.func_180656_a(var1, var2, var3, var4);
   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      if (!this.func_176406_l(var3)) {
         return 0;
      } else {
         return var3.func_177229_b(field_176387_N) == var4 ? this.func_176408_a(var1, var2, var3) : 0;
      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (this.func_176409_d(var1, var2)) {
         this.func_176398_g(var1, var2, var3);
      } else {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
         EnumFacing[] var5 = EnumFacing.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EnumFacing var8 = var5[var7];
            var1.func_175685_c(var2.func_177972_a(var8), this);
         }

      }
   }

   protected void func_176398_g(World var1, BlockPos var2, IBlockState var3) {
      if (!this.func_176405_b(var1, var2, var3)) {
         boolean var4 = this.func_176404_e(var1, var2, var3);
         if ((this.field_149914_a && !var4 || !this.field_149914_a && var4) && !var1.func_175691_a(var2, this)) {
            byte var5 = -1;
            if (this.func_176402_i(var1, var2, var3)) {
               var5 = -3;
            } else if (this.field_149914_a) {
               var5 = -2;
            }

            var1.func_175654_a(var2, this, this.func_176403_d(var3), var5);
         }

      }
   }

   public boolean func_176405_b(IBlockAccess var1, BlockPos var2, IBlockState var3) {
      return false;
   }

   protected boolean func_176404_e(World var1, BlockPos var2, IBlockState var3) {
      return this.func_176397_f(var1, var2, var3) > 0;
   }

   protected int func_176397_f(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176387_N);
      BlockPos var5 = var2.func_177972_a(var4);
      int var6 = var1.func_175651_c(var5, var4);
      if (var6 >= 15) {
         return var6;
      } else {
         IBlockState var7 = var1.func_180495_p(var5);
         return Math.max(var6, var7.func_177230_c() == Blocks.field_150488_af ? (Integer)var7.func_177229_b(BlockRedstoneWire.field_176351_O) : 0);
      }
   }

   protected int func_176407_c(IBlockAccess var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176387_N);
      EnumFacing var5 = var4.func_176746_e();
      EnumFacing var6 = var4.func_176735_f();
      return Math.max(this.func_176401_c(var1, var2.func_177972_a(var5), var5), this.func_176401_c(var1, var2.func_177972_a(var6), var6));
   }

   protected int func_176401_c(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      Block var5 = var4.func_177230_c();
      if (this.func_149908_a(var5)) {
         return var5 == Blocks.field_150488_af ? (Integer)var4.func_177229_b(BlockRedstoneWire.field_176351_O) : var1.func_175627_a(var2, var3);
      } else {
         return 0;
      }
   }

   public boolean func_149744_f() {
      return true;
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176387_N, var8.func_174811_aO().func_176734_d());
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (this.func_176404_e(var1, var2, var3)) {
         var1.func_175684_a(var2, this, 1);
      }

   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      this.func_176400_h(var1, var2, var3);
   }

   protected void func_176400_h(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176387_N);
      BlockPos var5 = var2.func_177972_a(var4.func_176734_d());
      var1.func_180496_d(var5, this);
      var1.func_175695_a(var5, this, var4);
   }

   public void func_176206_d(World var1, BlockPos var2, IBlockState var3) {
      if (this.field_149914_a) {
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing var7 = var4[var6];
            var1.func_175685_c(var2.func_177972_a(var7), this);
         }
      }

      super.func_176206_d(var1, var2, var3);
   }

   public boolean func_149662_c() {
      return false;
   }

   protected boolean func_149908_a(Block var1) {
      return var1.func_149744_f();
   }

   protected int func_176408_a(IBlockAccess var1, BlockPos var2, IBlockState var3) {
      return 15;
   }

   public static boolean func_149909_d(Block var0) {
      return Blocks.field_150413_aR.func_149907_e(var0) || Blocks.field_150441_bU.func_149907_e(var0);
   }

   public boolean func_149907_e(Block var1) {
      return var1 == this.func_180674_e(this.func_176223_P()).func_177230_c() || var1 == this.func_180675_k(this.func_176223_P()).func_177230_c();
   }

   public boolean func_176402_i(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = ((EnumFacing)var3.func_177229_b(field_176387_N)).func_176734_d();
      BlockPos var5 = var2.func_177972_a(var4);
      if (func_149909_d(var1.func_180495_p(var5).func_177230_c())) {
         return var1.func_180495_p(var5).func_177229_b(field_176387_N) != var4;
      } else {
         return false;
      }
   }

   protected int func_176399_m(IBlockState var1) {
      return this.func_176403_d(var1);
   }

   protected abstract int func_176403_d(IBlockState var1);

   protected abstract IBlockState func_180674_e(IBlockState var1);

   protected abstract IBlockState func_180675_k(IBlockState var1);

   public boolean func_149667_c(Block var1) {
      return this.func_149907_e(var1);
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }
}
