package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable {
   public static final PropertyInteger field_176484_a = PropertyInteger.func_177719_a("age", 0, 7);
   public static final PropertyDirection field_176483_b = PropertyDirection.func_177712_a("facing", new Predicate<EnumFacing>() {
      public boolean apply(EnumFacing var1) {
         return var1 != EnumFacing.DOWN;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((EnumFacing)var1);
      }
   });
   private final Block field_149877_a;

   protected BlockStem(Block var1) {
      super();
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176484_a, 0).func_177226_a(field_176483_b, EnumFacing.UP));
      this.field_149877_a = var1;
      this.func_149675_a(true);
      float var2 = 0.125F;
      this.func_149676_a(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.25F, 0.5F + var2);
      this.func_149647_a((CreativeTabs)null);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      var1 = var1.func_177226_a(field_176483_b, EnumFacing.UP);
      Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         EnumFacing var5 = (EnumFacing)var4.next();
         if (var2.func_180495_p(var3.func_177972_a(var5)).func_177230_c() == this.field_149877_a) {
            var1 = var1.func_177226_a(field_176483_b, var5);
            break;
         }
      }

      return var1;
   }

   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150458_ak;
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      super.func_180650_b(var1, var2, var3, var4);
      if (var1.func_175671_l(var2.func_177984_a()) >= 9) {
         float var5 = BlockCrops.func_180672_a(this, var1, var2);
         if (var4.nextInt((int)(25.0F / var5) + 1) == 0) {
            int var6 = (Integer)var3.func_177229_b(field_176484_a);
            if (var6 < 7) {
               var3 = var3.func_177226_a(field_176484_a, var6 + 1);
               var1.func_180501_a(var2, var3, 2);
            } else {
               Iterator var7 = EnumFacing.Plane.HORIZONTAL.iterator();

               while(var7.hasNext()) {
                  EnumFacing var8 = (EnumFacing)var7.next();
                  if (var1.func_180495_p(var2.func_177972_a(var8)).func_177230_c() == this.field_149877_a) {
                     return;
                  }
               }

               var2 = var2.func_177972_a(EnumFacing.Plane.HORIZONTAL.func_179518_a(var4));
               Block var9 = var1.func_180495_p(var2.func_177977_b()).func_177230_c();
               if (var1.func_180495_p(var2).func_177230_c().field_149764_J == Material.field_151579_a && (var9 == Blocks.field_150458_ak || var9 == Blocks.field_150346_d || var9 == Blocks.field_150349_c)) {
                  var1.func_175656_a(var2, this.field_149877_a.func_176223_P());
               }
            }
         }

      }
   }

   public void func_176482_g(World var1, BlockPos var2, IBlockState var3) {
      int var4 = (Integer)var3.func_177229_b(field_176484_a) + MathHelper.func_76136_a(var1.field_73012_v, 2, 5);
      var1.func_180501_a(var2, var3.func_177226_a(field_176484_a, Math.min(7, var4)), 2);
   }

   public int func_180644_h(IBlockState var1) {
      if (var1.func_177230_c() != this) {
         return super.func_180644_h(var1);
      } else {
         int var2 = (Integer)var1.func_177229_b(field_176484_a);
         int var3 = var2 * 32;
         int var4 = 255 - var2 * 8;
         int var5 = var2 * 4;
         return var3 << 16 | var4 << 8 | var5;
      }
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      return this.func_180644_h(var1.func_180495_p(var2));
   }

   public void func_149683_g() {
      float var1 = 0.125F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      this.field_149756_F = (double)((float)((Integer)var1.func_180495_p(var2).func_177229_b(field_176484_a) * 2 + 2) / 16.0F);
      float var3 = 0.125F;
      this.func_149676_a(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, (float)this.field_149756_F, 0.5F + var3);
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      super.func_180653_a(var1, var2, var3, var4, var5);
      if (!var1.field_72995_K) {
         Item var6 = this.func_176481_j();
         if (var6 != null) {
            int var7 = (Integer)var3.func_177229_b(field_176484_a);

            for(int var8 = 0; var8 < 3; ++var8) {
               if (var1.field_73012_v.nextInt(15) <= var7) {
                  func_180635_a(var1, var2, new ItemStack(var6));
               }
            }

         }
      }
   }

   protected Item func_176481_j() {
      if (this.field_149877_a == Blocks.field_150423_aK) {
         return Items.field_151080_bb;
      } else {
         return this.field_149877_a == Blocks.field_150440_ba ? Items.field_151081_bc : null;
      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return null;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      Item var3 = this.func_176481_j();
      return var3 != null ? var3 : null;
   }

   public boolean func_176473_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return (Integer)var3.func_177229_b(field_176484_a) != 7;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      this.func_176482_g(var1, var3, var4);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176484_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176484_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176484_a, field_176483_b});
   }
}
