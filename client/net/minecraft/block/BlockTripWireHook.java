package net.minecraft.block;

import com.google.common.base.Objects;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {
   public static final PropertyDirection field_176264_a;
   public static final PropertyBool field_176263_b;
   public static final PropertyBool field_176265_M;
   public static final PropertyBool field_176266_N;

   public BlockTripWireHook() {
      super(Material.field_151594_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176264_a, EnumFacing.NORTH).func_177226_a(field_176263_b, false).func_177226_a(field_176265_M, false).func_177226_a(field_176266_N, false));
      this.func_149647_a(CreativeTabs.field_78028_d);
      this.func_149675_a(true);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.func_177226_a(field_176266_N, !World.func_175683_a(var2, var3.func_177977_b()));
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

   public boolean func_176198_a(World var1, BlockPos var2, EnumFacing var3) {
      return var3.func_176740_k().func_176722_c() && var1.func_180495_p(var2.func_177972_a(var3.func_176734_d())).func_177230_c().func_149721_r();
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      Iterator var3 = EnumFacing.Plane.HORIZONTAL.iterator();

      EnumFacing var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (EnumFacing)var3.next();
      } while(!var1.func_180495_p(var2.func_177972_a(var4)).func_177230_c().func_149721_r());

      return true;
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var9 = this.func_176223_P().func_177226_a(field_176263_b, false).func_177226_a(field_176265_M, false).func_177226_a(field_176266_N, false);
      if (var3.func_176740_k().func_176722_c()) {
         var9 = var9.func_177226_a(field_176264_a, var3);
      }

      return var9;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      this.func_176260_a(var1, var2, var3, false, false, -1, (IBlockState)null);
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (var4 != this) {
         if (this.func_176261_e(var1, var2, var3)) {
            EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176264_a);
            if (!var1.func_180495_p(var2.func_177972_a(var5.func_176734_d())).func_177230_c().func_149721_r()) {
               this.func_176226_b(var1, var2, var3, 0);
               var1.func_175698_g(var2);
            }
         }

      }
   }

   public void func_176260_a(World var1, BlockPos var2, IBlockState var3, boolean var4, boolean var5, int var6, IBlockState var7) {
      EnumFacing var8 = (EnumFacing)var3.func_177229_b(field_176264_a);
      boolean var9 = (Boolean)var3.func_177229_b(field_176265_M);
      boolean var10 = (Boolean)var3.func_177229_b(field_176263_b);
      boolean var11 = !World.func_175683_a(var1, var2.func_177977_b());
      boolean var12 = !var4;
      boolean var13 = false;
      int var14 = 0;
      IBlockState[] var15 = new IBlockState[42];

      BlockPos var17;
      for(int var16 = 1; var16 < 42; ++var16) {
         var17 = var2.func_177967_a(var8, var16);
         IBlockState var18 = var1.func_180495_p(var17);
         if (var18.func_177230_c() == Blocks.field_150479_bC) {
            if (var18.func_177229_b(field_176264_a) == var8.func_176734_d()) {
               var14 = var16;
            }
            break;
         }

         if (var18.func_177230_c() != Blocks.field_150473_bD && var16 != var6) {
            var15[var16] = null;
            var12 = false;
         } else {
            if (var16 == var6) {
               var18 = (IBlockState)Objects.firstNonNull(var7, var18);
            }

            boolean var19 = !(Boolean)var18.func_177229_b(BlockTripWire.field_176295_N);
            boolean var20 = (Boolean)var18.func_177229_b(BlockTripWire.field_176293_a);
            boolean var21 = (Boolean)var18.func_177229_b(BlockTripWire.field_176290_b);
            var12 &= var21 == var11;
            var13 |= var19 && var20;
            var15[var16] = var18;
            if (var16 == var6) {
               var1.func_175684_a(var2, this, this.func_149738_a(var1));
               var12 &= var19;
            }
         }
      }

      var12 &= var14 > 1;
      var13 &= var12;
      IBlockState var22 = this.func_176223_P().func_177226_a(field_176265_M, var12).func_177226_a(field_176263_b, var13);
      if (var14 > 0) {
         var17 = var2.func_177967_a(var8, var14);
         EnumFacing var24 = var8.func_176734_d();
         var1.func_180501_a(var17, var22.func_177226_a(field_176264_a, var24), 3);
         this.func_176262_b(var1, var17, var24);
         this.func_180694_a(var1, var17, var12, var13, var9, var10);
      }

      this.func_180694_a(var1, var2, var12, var13, var9, var10);
      if (!var4) {
         var1.func_180501_a(var2, var22.func_177226_a(field_176264_a, var8), 3);
         if (var5) {
            this.func_176262_b(var1, var2, var8);
         }
      }

      if (var9 != var12) {
         for(int var23 = 1; var23 < var14; ++var23) {
            BlockPos var25 = var2.func_177967_a(var8, var23);
            IBlockState var26 = var15[var23];
            if (var26 != null && var1.func_180495_p(var25).func_177230_c() != Blocks.field_150350_a) {
               var1.func_180501_a(var25, var26.func_177226_a(field_176265_M, var12), 3);
            }
         }
      }

   }

   public void func_180645_a(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      this.func_176260_a(var1, var2, var3, false, true, -1, (IBlockState)null);
   }

   private void func_180694_a(World var1, BlockPos var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      if (var4 && !var6) {
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.1D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.4F, 0.6F);
      } else if (!var4 && var6) {
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.1D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.4F, 0.5F);
      } else if (var3 && !var5) {
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.1D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.4F, 0.7F);
      } else if (!var3 && var5) {
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.1D, (double)var2.func_177952_p() + 0.5D, "random.bowhit", 0.4F, 1.2F / (var1.field_73012_v.nextFloat() * 0.2F + 0.9F));
      }

   }

   private void func_176262_b(World var1, BlockPos var2, EnumFacing var3) {
      var1.func_175685_c(var2, this);
      var1.func_175685_c(var2.func_177972_a(var3.func_176734_d()), this);
   }

   private boolean func_176261_e(World var1, BlockPos var2, IBlockState var3) {
      if (!this.func_176196_c(var1, var2)) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
         return false;
      } else {
         return true;
      }
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      float var3 = 0.1875F;
      switch((EnumFacing)var1.func_180495_p(var2).func_177229_b(field_176264_a)) {
      case EAST:
         this.func_149676_a(0.0F, 0.2F, 0.5F - var3, var3 * 2.0F, 0.8F, 0.5F + var3);
         break;
      case WEST:
         this.func_149676_a(1.0F - var3 * 2.0F, 0.2F, 0.5F - var3, 1.0F, 0.8F, 0.5F + var3);
         break;
      case SOUTH:
         this.func_149676_a(0.5F - var3, 0.2F, 0.0F, 0.5F + var3, 0.8F, var3 * 2.0F);
         break;
      case NORTH:
         this.func_149676_a(0.5F - var3, 0.2F, 1.0F - var3 * 2.0F, 0.5F + var3, 0.8F, 1.0F);
      }

   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      boolean var4 = (Boolean)var3.func_177229_b(field_176265_M);
      boolean var5 = (Boolean)var3.func_177229_b(field_176263_b);
      if (var4 || var5) {
         this.func_176260_a(var1, var2, var3, true, false, -1, (IBlockState)null);
      }

      if (var5) {
         var1.func_175685_c(var2, this);
         var1.func_175685_c(var2.func_177972_a(((EnumFacing)var3.func_177229_b(field_176264_a)).func_176734_d()), this);
      }

      super.func_180663_b(var1, var2, var3);
   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return (Boolean)var3.func_177229_b(field_176263_b) ? 15 : 0;
   }

   public int func_176211_b(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      if (!(Boolean)var3.func_177229_b(field_176263_b)) {
         return 0;
      } else {
         return var3.func_177229_b(field_176264_a) == var4 ? 15 : 0;
      }
   }

   public boolean func_149744_f() {
      return true;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176264_a, EnumFacing.func_176731_b(var1 & 3)).func_177226_a(field_176263_b, (var1 & 8) > 0).func_177226_a(field_176265_M, (var1 & 4) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176264_a)).func_176736_b();
      if ((Boolean)var1.func_177229_b(field_176263_b)) {
         var3 |= 8;
      }

      if ((Boolean)var1.func_177229_b(field_176265_M)) {
         var3 |= 4;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176264_a, field_176263_b, field_176265_M, field_176266_N});
   }

   static {
      field_176264_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
      field_176263_b = PropertyBool.func_177716_a("powered");
      field_176265_M = PropertyBool.func_177716_a("attached");
      field_176266_N = PropertyBool.func_177716_a("suspended");
   }
}
