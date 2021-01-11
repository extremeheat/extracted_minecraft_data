package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockButton extends Block {
   public static final PropertyDirection field_176585_a = PropertyDirection.func_177714_a("facing");
   public static final PropertyBool field_176584_b = PropertyBool.func_177716_a("powered");
   private final boolean field_150047_a;

   protected BlockButton(boolean var1) {
      super(Material.field_151594_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176585_a, EnumFacing.NORTH).func_177226_a(field_176584_b, false));
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78028_d);
      this.field_150047_a = var1;
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public int func_149738_a(World var1) {
      return this.field_150047_a ? 30 : 20;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176198_a(World var1, BlockPos var2, EnumFacing var3) {
      return func_181088_a(var1, var2, var3.func_176734_d());
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing var6 = var3[var5];
         if (func_181088_a(var1, var2, var6)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean func_181088_a(World var0, BlockPos var1, EnumFacing var2) {
      BlockPos var3 = var1.func_177972_a(var2);
      return var2 == EnumFacing.DOWN ? World.func_175683_a(var0, var3) : var0.func_180495_p(var3).func_177230_c().func_149721_r();
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return func_181088_a(var1, var2, var3.func_176734_d()) ? this.func_176223_P().func_177226_a(field_176585_a, var3).func_177226_a(field_176584_b, false) : this.func_176223_P().func_177226_a(field_176585_a, EnumFacing.DOWN).func_177226_a(field_176584_b, false);
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (this.func_176583_e(var1, var2, var3) && !func_181088_a(var1, var2, ((EnumFacing)var3.func_177229_b(field_176585_a)).func_176734_d())) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
      }

   }

   private boolean func_176583_e(World var1, BlockPos var2, IBlockState var3) {
      if (this.func_176196_c(var1, var2)) {
         return true;
      } else {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
         return false;
      }
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      this.func_180681_d(var1.func_180495_p(var2));
   }

   private void func_180681_d(IBlockState var1) {
      EnumFacing var2 = (EnumFacing)var1.func_177229_b(field_176585_a);
      boolean var3 = (Boolean)var1.func_177229_b(field_176584_b);
      float var4 = 0.25F;
      float var5 = 0.375F;
      float var6 = (float)(var3 ? 1 : 2) / 16.0F;
      float var7 = 0.125F;
      float var8 = 0.1875F;
      switch(var2) {
      case EAST:
         this.func_149676_a(0.0F, 0.375F, 0.3125F, var6, 0.625F, 0.6875F);
         break;
      case WEST:
         this.func_149676_a(1.0F - var6, 0.375F, 0.3125F, 1.0F, 0.625F, 0.6875F);
         break;
      case SOUTH:
         this.func_149676_a(0.3125F, 0.375F, 0.0F, 0.6875F, 0.625F, var6);
         break;
      case NORTH:
         this.func_149676_a(0.3125F, 0.375F, 1.0F - var6, 0.6875F, 0.625F, 1.0F);
         break;
      case UP:
         this.func_149676_a(0.3125F, 0.0F, 0.375F, 0.6875F, 0.0F + var6, 0.625F);
         break;
      case DOWN:
         this.func_149676_a(0.3125F, 1.0F - var6, 0.375F, 0.6875F, 1.0F, 0.625F);
      }

   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if ((Boolean)var3.func_177229_b(field_176584_b)) {
         return true;
      } else {
         var1.func_180501_a(var2, var3.func_177226_a(field_176584_b, true), 3);
         var1.func_175704_b(var2, var2);
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.3F, 0.6F);
         this.func_176582_b(var1, var2, (EnumFacing)var3.func_177229_b(field_176585_a));
         var1.func_175684_a(var2, this, this.func_149738_a(var1));
         return true;
      }
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_176584_b)) {
         this.func_176582_b(var1, var2, (EnumFacing)var3.func_177229_b(field_176585_a));
      }

      super.func_180663_b(var1, var2, var3);
   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return (Boolean)var3.func_177229_b(field_176584_b) ? 15 : 0;
   }

   public int func_176211_b(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      if (!(Boolean)var3.func_177229_b(field_176584_b)) {
         return 0;
      } else {
         return var3.func_177229_b(field_176585_a) == var4 ? 15 : 0;
      }
   }

   public boolean func_149744_f() {
      return true;
   }

   public void func_180645_a(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         if ((Boolean)var3.func_177229_b(field_176584_b)) {
            if (this.field_150047_a) {
               this.func_180680_f(var1, var2, var3);
            } else {
               var1.func_175656_a(var2, var3.func_177226_a(field_176584_b, false));
               this.func_176582_b(var1, var2, (EnumFacing)var3.func_177229_b(field_176585_a));
               var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.3F, 0.5F);
               var1.func_175704_b(var2, var2);
            }

         }
      }
   }

   public void func_149683_g() {
      float var1 = 0.1875F;
      float var2 = 0.125F;
      float var3 = 0.125F;
      this.func_149676_a(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (!var1.field_72995_K) {
         if (this.field_150047_a) {
            if (!(Boolean)var3.func_177229_b(field_176584_b)) {
               this.func_180680_f(var1, var2, var3);
            }
         }
      }
   }

   private void func_180680_f(World var1, BlockPos var2, IBlockState var3) {
      this.func_180681_d(var3);
      List var4 = var1.func_72872_a(EntityArrow.class, new AxisAlignedBB((double)var2.func_177958_n() + this.field_149759_B, (double)var2.func_177956_o() + this.field_149760_C, (double)var2.func_177952_p() + this.field_149754_D, (double)var2.func_177958_n() + this.field_149755_E, (double)var2.func_177956_o() + this.field_149756_F, (double)var2.func_177952_p() + this.field_149757_G));
      boolean var5 = !var4.isEmpty();
      boolean var6 = (Boolean)var3.func_177229_b(field_176584_b);
      if (var5 && !var6) {
         var1.func_175656_a(var2, var3.func_177226_a(field_176584_b, true));
         this.func_176582_b(var1, var2, (EnumFacing)var3.func_177229_b(field_176585_a));
         var1.func_175704_b(var2, var2);
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.3F, 0.6F);
      }

      if (!var5 && var6) {
         var1.func_175656_a(var2, var3.func_177226_a(field_176584_b, false));
         this.func_176582_b(var1, var2, (EnumFacing)var3.func_177229_b(field_176585_a));
         var1.func_175704_b(var2, var2);
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.3F, 0.5F);
      }

      if (var5) {
         var1.func_175684_a(var2, this, this.func_149738_a(var1));
      }

   }

   private void func_176582_b(World var1, BlockPos var2, EnumFacing var3) {
      var1.func_175685_c(var2, this);
      var1.func_175685_c(var2.func_177972_a(var3.func_176734_d()), this);
   }

   public IBlockState func_176203_a(int var1) {
      EnumFacing var2;
      switch(var1 & 7) {
      case 0:
         var2 = EnumFacing.DOWN;
         break;
      case 1:
         var2 = EnumFacing.EAST;
         break;
      case 2:
         var2 = EnumFacing.WEST;
         break;
      case 3:
         var2 = EnumFacing.SOUTH;
         break;
      case 4:
         var2 = EnumFacing.NORTH;
         break;
      case 5:
      default:
         var2 = EnumFacing.UP;
      }

      return this.func_176223_P().func_177226_a(field_176585_a, var2).func_177226_a(field_176584_b, (var1 & 8) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      int var2;
      switch((EnumFacing)var1.func_177229_b(field_176585_a)) {
      case EAST:
         var2 = 1;
         break;
      case WEST:
         var2 = 2;
         break;
      case SOUTH:
         var2 = 3;
         break;
      case NORTH:
         var2 = 4;
         break;
      case UP:
      default:
         var2 = 5;
         break;
      case DOWN:
         var2 = 0;
      }

      if ((Boolean)var1.func_177229_b(field_176584_b)) {
         var2 |= 8;
      }

      return var2;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176585_a, field_176584_b});
   }
}
