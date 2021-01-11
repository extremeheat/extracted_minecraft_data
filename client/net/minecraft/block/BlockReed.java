package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReed extends Block {
   public static final PropertyInteger field_176355_a = PropertyInteger.func_177719_a("age", 0, 15);

   protected BlockReed() {
      super(Material.field_151585_k);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176355_a, 0));
      float var1 = 0.375F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 1.0F, 0.5F + var1);
      this.func_149675_a(true);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var1.func_180495_p(var2.func_177977_b()).func_177230_c() == Blocks.field_150436_aH || this.func_176353_e(var1, var2, var3)) {
         if (var1.func_175623_d(var2.func_177984_a())) {
            int var5;
            for(var5 = 1; var1.func_180495_p(var2.func_177979_c(var5)).func_177230_c() == this; ++var5) {
            }

            if (var5 < 3) {
               int var6 = (Integer)var3.func_177229_b(field_176355_a);
               if (var6 == 15) {
                  var1.func_175656_a(var2.func_177984_a(), this.func_176223_P());
                  var1.func_180501_a(var2, var3.func_177226_a(field_176355_a, 0), 4);
               } else {
                  var1.func_180501_a(var2, var3.func_177226_a(field_176355_a, var6 + 1), 4);
               }
            }
         }

      }
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      Block var3 = var1.func_180495_p(var2.func_177977_b()).func_177230_c();
      if (var3 == this) {
         return true;
      } else if (var3 != Blocks.field_150349_c && var3 != Blocks.field_150346_d && var3 != Blocks.field_150354_m) {
         return false;
      } else {
         Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();

         EnumFacing var5;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            var5 = (EnumFacing)var4.next();
         } while(var1.func_180495_p(var2.func_177972_a(var5).func_177977_b()).func_177230_c().func_149688_o() != Material.field_151586_h);

         return true;
      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.func_176353_e(var1, var2, var3);
   }

   protected final boolean func_176353_e(World var1, BlockPos var2, IBlockState var3) {
      if (this.func_176354_d(var1, var2)) {
         return true;
      } else {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
         return false;
      }
   }

   public boolean func_176354_d(World var1, BlockPos var2) {
      return this.func_176196_c(var1, var2);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151120_aE;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151120_aE;
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      return var1.func_180494_b(var2).func_180627_b(var2);
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176355_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176355_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176355_a});
   }
}
