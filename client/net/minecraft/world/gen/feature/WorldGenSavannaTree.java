package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenSavannaTree extends WorldGenAbstractTree {
   private static final IBlockState field_181643_a;
   private static final IBlockState field_181644_b;

   public WorldGenSavannaTree(boolean var1) {
      super(var1);
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      int var4 = var2.nextInt(3) + var2.nextInt(3) + 5;
      boolean var5 = true;
      if (var3.func_177956_o() >= 1 && var3.func_177956_o() + var4 + 1 <= 256) {
         int var9;
         int var10;
         for(int var6 = var3.func_177956_o(); var6 <= var3.func_177956_o() + 1 + var4; ++var6) {
            byte var7 = 1;
            if (var6 == var3.func_177956_o()) {
               var7 = 0;
            }

            if (var6 >= var3.func_177956_o() + 1 + var4 - 2) {
               var7 = 2;
            }

            BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

            for(var9 = var3.func_177958_n() - var7; var9 <= var3.func_177958_n() + var7 && var5; ++var9) {
               for(var10 = var3.func_177952_p() - var7; var10 <= var3.func_177952_p() + var7 && var5; ++var10) {
                  if (var6 >= 0 && var6 < 256) {
                     if (!this.func_150523_a(var1.func_180495_p(var8.func_181079_c(var9, var6, var10)).func_177230_c())) {
                        var5 = false;
                     }
                  } else {
                     var5 = false;
                  }
               }
            }
         }

         if (!var5) {
            return false;
         } else {
            Block var20 = var1.func_180495_p(var3.func_177977_b()).func_177230_c();
            if ((var20 == Blocks.field_150349_c || var20 == Blocks.field_150346_d) && var3.func_177956_o() < 256 - var4 - 1) {
               this.func_175921_a(var1, var3.func_177977_b());
               EnumFacing var21 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var2);
               int var22 = var4 - var2.nextInt(4) - 1;
               var9 = 3 - var2.nextInt(3);
               var10 = var3.func_177958_n();
               int var11 = var3.func_177952_p();
               int var12 = 0;

               int var14;
               for(int var13 = 0; var13 < var4; ++var13) {
                  var14 = var3.func_177956_o() + var13;
                  if (var13 >= var22 && var9 > 0) {
                     var10 += var21.func_82601_c();
                     var11 += var21.func_82599_e();
                     --var9;
                  }

                  BlockPos var15 = new BlockPos(var10, var14, var11);
                  Material var16 = var1.func_180495_p(var15).func_177230_c().func_149688_o();
                  if (var16 == Material.field_151579_a || var16 == Material.field_151584_j) {
                     this.func_181642_b(var1, var15);
                     var12 = var14;
                  }
               }

               BlockPos var23 = new BlockPos(var10, var12, var11);

               int var25;
               for(var14 = -3; var14 <= 3; ++var14) {
                  for(var25 = -3; var25 <= 3; ++var25) {
                     if (Math.abs(var14) != 3 || Math.abs(var25) != 3) {
                        this.func_175924_b(var1, var23.func_177982_a(var14, 0, var25));
                     }
                  }
               }

               var23 = var23.func_177984_a();

               for(var14 = -1; var14 <= 1; ++var14) {
                  for(var25 = -1; var25 <= 1; ++var25) {
                     this.func_175924_b(var1, var23.func_177982_a(var14, 0, var25));
                  }
               }

               this.func_175924_b(var1, var23.func_177965_g(2));
               this.func_175924_b(var1, var23.func_177985_f(2));
               this.func_175924_b(var1, var23.func_177970_e(2));
               this.func_175924_b(var1, var23.func_177964_d(2));
               var10 = var3.func_177958_n();
               var11 = var3.func_177952_p();
               EnumFacing var24 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var2);
               if (var24 != var21) {
                  var14 = var22 - var2.nextInt(2) - 1;
                  var25 = 1 + var2.nextInt(3);
                  var12 = 0;

                  int var17;
                  for(int var26 = var14; var26 < var4 && var25 > 0; --var25) {
                     if (var26 >= 1) {
                        var17 = var3.func_177956_o() + var26;
                        var10 += var24.func_82601_c();
                        var11 += var24.func_82599_e();
                        BlockPos var18 = new BlockPos(var10, var17, var11);
                        Material var19 = var1.func_180495_p(var18).func_177230_c().func_149688_o();
                        if (var19 == Material.field_151579_a || var19 == Material.field_151584_j) {
                           this.func_181642_b(var1, var18);
                           var12 = var17;
                        }
                     }

                     ++var26;
                  }

                  if (var12 > 0) {
                     BlockPos var27 = new BlockPos(var10, var12, var11);

                     int var28;
                     for(var17 = -2; var17 <= 2; ++var17) {
                        for(var28 = -2; var28 <= 2; ++var28) {
                           if (Math.abs(var17) != 2 || Math.abs(var28) != 2) {
                              this.func_175924_b(var1, var27.func_177982_a(var17, 0, var28));
                           }
                        }
                     }

                     var27 = var27.func_177984_a();

                     for(var17 = -1; var17 <= 1; ++var17) {
                        for(var28 = -1; var28 <= 1; ++var28) {
                           this.func_175924_b(var1, var27.func_177982_a(var17, 0, var28));
                        }
                     }
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private void func_181642_b(World var1, BlockPos var2) {
      this.func_175903_a(var1, var2, field_181643_a);
   }

   private void func_175924_b(World var1, BlockPos var2) {
      Material var3 = var1.func_180495_p(var2).func_177230_c().func_149688_o();
      if (var3 == Material.field_151579_a || var3 == Material.field_151584_j) {
         this.func_175903_a(var1, var2, field_181644_b);
      }

   }

   static {
      field_181643_a = Blocks.field_150363_s.func_176223_P().func_177226_a(BlockNewLog.field_176300_b, BlockPlanks.EnumType.ACACIA);
      field_181644_b = Blocks.field_150361_u.func_176223_P().func_177226_a(BlockNewLeaf.field_176240_P, BlockPlanks.EnumType.ACACIA).func_177226_a(BlockLeaves.field_176236_b, false);
   }
}
