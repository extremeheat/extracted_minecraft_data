package net.minecraft.block;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid {
   int field_149815_a;

   protected BlockDynamicLiquid(Material var1) {
      super(var1);
   }

   private void func_180690_f(World var1, BlockPos var2, IBlockState var3) {
      var1.func_180501_a(var2, func_176363_b(this.field_149764_J).func_176223_P().func_177226_a(field_176367_b, var3.func_177229_b(field_176367_b)), 2);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      int var5 = (Integer)var3.func_177229_b(field_176367_b);
      byte var6 = 1;
      if (this.field_149764_J == Material.field_151587_i && !var1.field_73011_w.func_177500_n()) {
         var6 = 2;
      }

      int var7 = this.func_149738_a(var1);
      int var15;
      if (var5 > 0) {
         int var8 = -100;
         this.field_149815_a = 0;

         EnumFacing var10;
         for(Iterator var9 = EnumFacing.Plane.HORIZONTAL.iterator(); var9.hasNext(); var8 = this.func_176371_a(var1, var2.func_177972_a(var10), var8)) {
            var10 = (EnumFacing)var9.next();
         }

         int var14 = var8 + var6;
         if (var14 >= 8 || var8 < 0) {
            var14 = -1;
         }

         if (this.func_176362_e(var1, var2.func_177984_a()) >= 0) {
            var15 = this.func_176362_e(var1, var2.func_177984_a());
            if (var15 >= 8) {
               var14 = var15;
            } else {
               var14 = var15 + 8;
            }
         }

         if (this.field_149815_a >= 2 && this.field_149764_J == Material.field_151586_h) {
            IBlockState var16 = var1.func_180495_p(var2.func_177977_b());
            if (var16.func_177230_c().func_149688_o().func_76220_a()) {
               var14 = 0;
            } else if (var16.func_177230_c().func_149688_o() == this.field_149764_J && (Integer)var16.func_177229_b(field_176367_b) == 0) {
               var14 = 0;
            }
         }

         if (this.field_149764_J == Material.field_151587_i && var5 < 8 && var14 < 8 && var14 > var5 && var4.nextInt(4) != 0) {
            var7 *= 4;
         }

         if (var14 == var5) {
            this.func_180690_f(var1, var2, var3);
         } else {
            var5 = var14;
            if (var14 < 0) {
               var1.func_175698_g(var2);
            } else {
               var3 = var3.func_177226_a(field_176367_b, var14);
               var1.func_180501_a(var2, var3, 2);
               var1.func_175684_a(var2, this, var7);
               var1.func_175685_c(var2, this);
            }
         }
      } else {
         this.func_180690_f(var1, var2, var3);
      }

      IBlockState var13 = var1.func_180495_p(var2.func_177977_b());
      if (this.func_176373_h(var1, var2.func_177977_b(), var13)) {
         if (this.field_149764_J == Material.field_151587_i && var1.func_180495_p(var2.func_177977_b()).func_177230_c().func_149688_o() == Material.field_151586_h) {
            var1.func_175656_a(var2.func_177977_b(), Blocks.field_150348_b.func_176223_P());
            this.func_180688_d(var1, var2.func_177977_b());
            return;
         }

         if (var5 >= 8) {
            this.func_176375_a(var1, var2.func_177977_b(), var13, var5);
         } else {
            this.func_176375_a(var1, var2.func_177977_b(), var13, var5 + 8);
         }
      } else if (var5 >= 0 && (var5 == 0 || this.func_176372_g(var1, var2.func_177977_b(), var13))) {
         Set var17 = this.func_176376_e(var1, var2);
         var15 = var5 + var6;
         if (var5 >= 8) {
            var15 = 1;
         }

         if (var15 >= 8) {
            return;
         }

         Iterator var11 = var17.iterator();

         while(var11.hasNext()) {
            EnumFacing var12 = (EnumFacing)var11.next();
            this.func_176375_a(var1, var2.func_177972_a(var12), var1.func_180495_p(var2.func_177972_a(var12)), var15);
         }
      }

   }

   private void func_176375_a(World var1, BlockPos var2, IBlockState var3, int var4) {
      if (this.func_176373_h(var1, var2, var3)) {
         if (var3.func_177230_c() != Blocks.field_150350_a) {
            if (this.field_149764_J == Material.field_151587_i) {
               this.func_180688_d(var1, var2);
            } else {
               var3.func_177230_c().func_176226_b(var1, var2, var3, 0);
            }
         }

         var1.func_180501_a(var2, this.func_176223_P().func_177226_a(field_176367_b, var4), 3);
      }

   }

   private int func_176374_a(World var1, BlockPos var2, int var3, EnumFacing var4) {
      int var5 = 1000;
      Iterator var6 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(true) {
         EnumFacing var7;
         BlockPos var8;
         IBlockState var9;
         do {
            do {
               do {
                  if (!var6.hasNext()) {
                     return var5;
                  }

                  var7 = (EnumFacing)var6.next();
               } while(var7 == var4);

               var8 = var2.func_177972_a(var7);
               var9 = var1.func_180495_p(var8);
            } while(this.func_176372_g(var1, var8, var9));
         } while(var9.func_177230_c().func_149688_o() == this.field_149764_J && (Integer)var9.func_177229_b(field_176367_b) <= 0);

         if (!this.func_176372_g(var1, var8.func_177977_b(), var9)) {
            return var3;
         }

         if (var3 < 4) {
            int var10 = this.func_176374_a(var1, var8, var3 + 1, var7.func_176734_d());
            if (var10 < var5) {
               var5 = var10;
            }
         }
      }
   }

   private Set<EnumFacing> func_176376_e(World var1, BlockPos var2) {
      int var3 = 1000;
      EnumSet var4 = EnumSet.noneOf(EnumFacing.class);
      Iterator var5 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(true) {
         EnumFacing var6;
         BlockPos var7;
         IBlockState var8;
         do {
            do {
               if (!var5.hasNext()) {
                  return var4;
               }

               var6 = (EnumFacing)var5.next();
               var7 = var2.func_177972_a(var6);
               var8 = var1.func_180495_p(var7);
            } while(this.func_176372_g(var1, var7, var8));
         } while(var8.func_177230_c().func_149688_o() == this.field_149764_J && (Integer)var8.func_177229_b(field_176367_b) <= 0);

         int var9;
         if (this.func_176372_g(var1, var7.func_177977_b(), var1.func_180495_p(var7.func_177977_b()))) {
            var9 = this.func_176374_a(var1, var7, 1, var6.func_176734_d());
         } else {
            var9 = 0;
         }

         if (var9 < var3) {
            var4.clear();
         }

         if (var9 <= var3) {
            var4.add(var6);
            var3 = var9;
         }
      }
   }

   private boolean func_176372_g(World var1, BlockPos var2, IBlockState var3) {
      Block var4 = var1.func_180495_p(var2).func_177230_c();
      if (!(var4 instanceof BlockDoor) && var4 != Blocks.field_150472_an && var4 != Blocks.field_150468_ap && var4 != Blocks.field_150436_aH) {
         return var4.field_149764_J == Material.field_151567_E ? true : var4.field_149764_J.func_76230_c();
      } else {
         return true;
      }
   }

   protected int func_176371_a(World var1, BlockPos var2, int var3) {
      int var4 = this.func_176362_e(var1, var2);
      if (var4 < 0) {
         return var3;
      } else {
         if (var4 == 0) {
            ++this.field_149815_a;
         }

         if (var4 >= 8) {
            var4 = 0;
         }

         return var3 >= 0 && var4 >= var3 ? var3 : var4;
      }
   }

   private boolean func_176373_h(World var1, BlockPos var2, IBlockState var3) {
      Material var4 = var3.func_177230_c().func_149688_o();
      return var4 != this.field_149764_J && var4 != Material.field_151587_i && !this.func_176372_g(var1, var2, var3);
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      if (!this.func_176365_e(var1, var2, var3)) {
         var1.func_175684_a(var2, this, this.func_149738_a(var1));
      }

   }
}
