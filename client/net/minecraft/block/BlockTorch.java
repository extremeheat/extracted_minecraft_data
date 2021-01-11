package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTorch extends Block {
   public static final PropertyDirection field_176596_a = PropertyDirection.func_177712_a("facing", new Predicate<EnumFacing>() {
      public boolean apply(EnumFacing var1) {
         return var1 != EnumFacing.DOWN;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((EnumFacing)var1);
      }
   });

   protected BlockTorch() {
      super(Material.field_151594_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176596_a, EnumFacing.UP));
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
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

   private boolean func_176594_d(World var1, BlockPos var2) {
      if (World.func_175683_a(var1, var2)) {
         return true;
      } else {
         Block var3 = var1.func_180495_p(var2).func_177230_c();
         return var3 instanceof BlockFence || var3 == Blocks.field_150359_w || var3 == Blocks.field_150463_bK || var3 == Blocks.field_150399_cn;
      }
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      Iterator var3 = field_176596_a.func_177700_c().iterator();

      EnumFacing var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (EnumFacing)var3.next();
      } while(!this.func_176595_b(var1, var2, var4));

      return true;
   }

   private boolean func_176595_b(World var1, BlockPos var2, EnumFacing var3) {
      BlockPos var4 = var2.func_177972_a(var3.func_176734_d());
      boolean var5 = var3.func_176740_k().func_176722_c();
      return var5 && var1.func_175677_d(var4, true) || var3.equals(EnumFacing.UP) && this.func_176594_d(var1, var4);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      if (this.func_176595_b(var1, var2, var3)) {
         return this.func_176223_P().func_177226_a(field_176596_a, var3);
      } else {
         Iterator var9 = EnumFacing.Plane.HORIZONTAL.iterator();

         EnumFacing var10;
         do {
            if (!var9.hasNext()) {
               return this.func_176223_P();
            }

            var10 = (EnumFacing)var9.next();
         } while(!var1.func_175677_d(var2.func_177972_a(var10.func_176734_d()), true));

         return this.func_176223_P().func_177226_a(field_176596_a, var10);
      }
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      this.func_176593_f(var1, var2, var3);
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.func_176592_e(var1, var2, var3);
   }

   protected boolean func_176592_e(World var1, BlockPos var2, IBlockState var3) {
      if (!this.func_176593_f(var1, var2, var3)) {
         return true;
      } else {
         EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176596_a);
         EnumFacing.Axis var5 = var4.func_176740_k();
         EnumFacing var6 = var4.func_176734_d();
         boolean var7 = false;
         if (var5.func_176722_c() && !var1.func_175677_d(var2.func_177972_a(var6), true)) {
            var7 = true;
         } else if (var5.func_176720_b() && !this.func_176594_d(var1, var2.func_177972_a(var6))) {
            var7 = true;
         }

         if (var7) {
            this.func_176226_b(var1, var2, var3, 0);
            var1.func_175698_g(var2);
            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean func_176593_f(World var1, BlockPos var2, IBlockState var3) {
      if (var3.func_177230_c() == this && this.func_176595_b(var1, var2, (EnumFacing)var3.func_177229_b(field_176596_a))) {
         return true;
      } else {
         if (var1.func_180495_p(var2).func_177230_c() == this) {
            this.func_176226_b(var1, var2, var3, 0);
            var1.func_175698_g(var2);
         }

         return false;
      }
   }

   public MovingObjectPosition func_180636_a(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      EnumFacing var5 = (EnumFacing)var1.func_180495_p(var2).func_177229_b(field_176596_a);
      float var6 = 0.15F;
      if (var5 == EnumFacing.EAST) {
         this.func_149676_a(0.0F, 0.2F, 0.5F - var6, var6 * 2.0F, 0.8F, 0.5F + var6);
      } else if (var5 == EnumFacing.WEST) {
         this.func_149676_a(1.0F - var6 * 2.0F, 0.2F, 0.5F - var6, 1.0F, 0.8F, 0.5F + var6);
      } else if (var5 == EnumFacing.SOUTH) {
         this.func_149676_a(0.5F - var6, 0.2F, 0.0F, 0.5F + var6, 0.8F, var6 * 2.0F);
      } else if (var5 == EnumFacing.NORTH) {
         this.func_149676_a(0.5F - var6, 0.2F, 1.0F - var6 * 2.0F, 0.5F + var6, 0.8F, 1.0F);
      } else {
         var6 = 0.1F;
         this.func_149676_a(0.5F - var6, 0.0F, 0.5F - var6, 0.5F + var6, 0.6F, 0.5F + var6);
      }

      return super.func_180636_a(var1, var2, var3, var4);
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176596_a);
      double var6 = (double)var2.func_177958_n() + 0.5D;
      double var8 = (double)var2.func_177956_o() + 0.7D;
      double var10 = (double)var2.func_177952_p() + 0.5D;
      double var12 = 0.22D;
      double var14 = 0.27D;
      if (var5.func_176740_k().func_176722_c()) {
         EnumFacing var16 = var5.func_176734_d();
         var1.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var6 + var14 * (double)var16.func_82601_c(), var8 + var12, var10 + var14 * (double)var16.func_82599_e(), 0.0D, 0.0D, 0.0D);
         var1.func_175688_a(EnumParticleTypes.FLAME, var6 + var14 * (double)var16.func_82601_c(), var8 + var12, var10 + var14 * (double)var16.func_82599_e(), 0.0D, 0.0D, 0.0D);
      } else {
         var1.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var6, var8, var10, 0.0D, 0.0D, 0.0D);
         var1.func_175688_a(EnumParticleTypes.FLAME, var6, var8, var10, 0.0D, 0.0D, 0.0D);
      }

   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      IBlockState var2 = this.func_176223_P();
      switch(var1) {
      case 1:
         var2 = var2.func_177226_a(field_176596_a, EnumFacing.EAST);
         break;
      case 2:
         var2 = var2.func_177226_a(field_176596_a, EnumFacing.WEST);
         break;
      case 3:
         var2 = var2.func_177226_a(field_176596_a, EnumFacing.SOUTH);
         break;
      case 4:
         var2 = var2.func_177226_a(field_176596_a, EnumFacing.NORTH);
         break;
      case 5:
      default:
         var2 = var2.func_177226_a(field_176596_a, EnumFacing.UP);
      }

      return var2;
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3;
      switch((EnumFacing)var1.func_177229_b(field_176596_a)) {
      case EAST:
         var3 = var2 | 1;
         break;
      case WEST:
         var3 = var2 | 2;
         break;
      case SOUTH:
         var3 = var2 | 3;
         break;
      case NORTH:
         var3 = var2 | 4;
         break;
      case DOWN:
      case UP:
      default:
         var3 = var2 | 5;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176596_a});
   }
}
