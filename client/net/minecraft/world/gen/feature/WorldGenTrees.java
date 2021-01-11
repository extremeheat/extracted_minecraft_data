package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenTrees extends WorldGenAbstractTree {
   private static final IBlockState field_181653_a;
   private static final IBlockState field_181654_b;
   private final int field_76533_a;
   private final boolean field_76531_b;
   private final IBlockState field_76532_c;
   private final IBlockState field_76530_d;

   public WorldGenTrees(boolean var1) {
      this(var1, 4, field_181653_a, field_181654_b, false);
   }

   public WorldGenTrees(boolean var1, int var2, IBlockState var3, IBlockState var4, boolean var5) {
      super(var1);
      this.field_76533_a = var2;
      this.field_76532_c = var3;
      this.field_76530_d = var4;
      this.field_76531_b = var5;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      int var4 = var2.nextInt(3) + this.field_76533_a;
      boolean var5 = true;
      if (var3.func_177956_o() >= 1 && var3.func_177956_o() + var4 + 1 <= 256) {
         byte var7;
         int var9;
         int var10;
         for(int var6 = var3.func_177956_o(); var6 <= var3.func_177956_o() + 1 + var4; ++var6) {
            var7 = 1;
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
            Block var19 = var1.func_180495_p(var3.func_177977_b()).func_177230_c();
            if ((var19 == Blocks.field_150349_c || var19 == Blocks.field_150346_d || var19 == Blocks.field_150458_ak) && var3.func_177956_o() < 256 - var4 - 1) {
               this.func_175921_a(var1, var3.func_177977_b());
               var7 = 3;
               byte var20 = 0;

               int var11;
               int var13;
               int var14;
               BlockPos var16;
               for(var9 = var3.func_177956_o() - var7 + var4; var9 <= var3.func_177956_o() + var4; ++var9) {
                  var10 = var9 - (var3.func_177956_o() + var4);
                  var11 = var20 + 1 - var10 / 2;

                  for(int var12 = var3.func_177958_n() - var11; var12 <= var3.func_177958_n() + var11; ++var12) {
                     var13 = var12 - var3.func_177958_n();

                     for(var14 = var3.func_177952_p() - var11; var14 <= var3.func_177952_p() + var11; ++var14) {
                        int var15 = var14 - var3.func_177952_p();
                        if (Math.abs(var13) != var11 || Math.abs(var15) != var11 || var2.nextInt(2) != 0 && var10 != 0) {
                           var16 = new BlockPos(var12, var9, var14);
                           Block var17 = var1.func_180495_p(var16).func_177230_c();
                           if (var17.func_149688_o() == Material.field_151579_a || var17.func_149688_o() == Material.field_151584_j || var17.func_149688_o() == Material.field_151582_l) {
                              this.func_175903_a(var1, var16, this.field_76530_d);
                           }
                        }
                     }
                  }
               }

               for(var9 = 0; var9 < var4; ++var9) {
                  Block var21 = var1.func_180495_p(var3.func_177981_b(var9)).func_177230_c();
                  if (var21.func_149688_o() == Material.field_151579_a || var21.func_149688_o() == Material.field_151584_j || var21.func_149688_o() == Material.field_151582_l) {
                     this.func_175903_a(var1, var3.func_177981_b(var9), this.field_76532_c);
                     if (this.field_76531_b && var9 > 0) {
                        if (var2.nextInt(3) > 0 && var1.func_175623_d(var3.func_177982_a(-1, var9, 0))) {
                           this.func_181651_a(var1, var3.func_177982_a(-1, var9, 0), BlockVine.field_176278_M);
                        }

                        if (var2.nextInt(3) > 0 && var1.func_175623_d(var3.func_177982_a(1, var9, 0))) {
                           this.func_181651_a(var1, var3.func_177982_a(1, var9, 0), BlockVine.field_176280_O);
                        }

                        if (var2.nextInt(3) > 0 && var1.func_175623_d(var3.func_177982_a(0, var9, -1))) {
                           this.func_181651_a(var1, var3.func_177982_a(0, var9, -1), BlockVine.field_176279_N);
                        }

                        if (var2.nextInt(3) > 0 && var1.func_175623_d(var3.func_177982_a(0, var9, 1))) {
                           this.func_181651_a(var1, var3.func_177982_a(0, var9, 1), BlockVine.field_176273_b);
                        }
                     }
                  }
               }

               if (this.field_76531_b) {
                  for(var9 = var3.func_177956_o() - 3 + var4; var9 <= var3.func_177956_o() + var4; ++var9) {
                     var10 = var9 - (var3.func_177956_o() + var4);
                     var11 = 2 - var10 / 2;
                     BlockPos.MutableBlockPos var24 = new BlockPos.MutableBlockPos();

                     for(var13 = var3.func_177958_n() - var11; var13 <= var3.func_177958_n() + var11; ++var13) {
                        for(var14 = var3.func_177952_p() - var11; var14 <= var3.func_177952_p() + var11; ++var14) {
                           var24.func_181079_c(var13, var9, var14);
                           if (var1.func_180495_p(var24).func_177230_c().func_149688_o() == Material.field_151584_j) {
                              BlockPos var26 = var24.func_177976_e();
                              var16 = var24.func_177974_f();
                              BlockPos var27 = var24.func_177978_c();
                              BlockPos var18 = var24.func_177968_d();
                              if (var2.nextInt(4) == 0 && var1.func_180495_p(var26).func_177230_c().func_149688_o() == Material.field_151579_a) {
                                 this.func_181650_b(var1, var26, BlockVine.field_176278_M);
                              }

                              if (var2.nextInt(4) == 0 && var1.func_180495_p(var16).func_177230_c().func_149688_o() == Material.field_151579_a) {
                                 this.func_181650_b(var1, var16, BlockVine.field_176280_O);
                              }

                              if (var2.nextInt(4) == 0 && var1.func_180495_p(var27).func_177230_c().func_149688_o() == Material.field_151579_a) {
                                 this.func_181650_b(var1, var27, BlockVine.field_176279_N);
                              }

                              if (var2.nextInt(4) == 0 && var1.func_180495_p(var18).func_177230_c().func_149688_o() == Material.field_151579_a) {
                                 this.func_181650_b(var1, var18, BlockVine.field_176273_b);
                              }
                           }
                        }
                     }
                  }

                  if (var2.nextInt(5) == 0 && var4 > 5) {
                     for(var9 = 0; var9 < 2; ++var9) {
                        Iterator var22 = EnumFacing.Plane.HORIZONTAL.iterator();

                        while(var22.hasNext()) {
                           EnumFacing var23 = (EnumFacing)var22.next();
                           if (var2.nextInt(4 - var9) == 0) {
                              EnumFacing var25 = var23.func_176734_d();
                              this.func_181652_a(var1, var2.nextInt(3), var3.func_177982_a(var25.func_82601_c(), var4 - 5 + var9, var25.func_82599_e()), var23);
                           }
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

   private void func_181652_a(World var1, int var2, BlockPos var3, EnumFacing var4) {
      this.func_175903_a(var1, var3, Blocks.field_150375_by.func_176223_P().func_177226_a(BlockCocoa.field_176501_a, var2).func_177226_a(BlockCocoa.field_176387_N, var4));
   }

   private void func_181651_a(World var1, BlockPos var2, PropertyBool var3) {
      this.func_175903_a(var1, var2, Blocks.field_150395_bd.func_176223_P().func_177226_a(var3, true));
   }

   private void func_181650_b(World var1, BlockPos var2, PropertyBool var3) {
      this.func_181651_a(var1, var2, var3);
      int var4 = 4;

      for(var2 = var2.func_177977_b(); var1.func_180495_p(var2).func_177230_c().func_149688_o() == Material.field_151579_a && var4 > 0; --var4) {
         this.func_181651_a(var1, var2, var3);
         var2 = var2.func_177977_b();
      }

   }

   static {
      field_181653_a = Blocks.field_150364_r.func_176223_P().func_177226_a(BlockOldLog.field_176301_b, BlockPlanks.EnumType.OAK);
      field_181654_b = Blocks.field_150362_t.func_176223_P().func_177226_a(BlockOldLeaf.field_176239_P, BlockPlanks.EnumType.OAK).func_177226_a(BlockLeaves.field_176236_b, false);
   }
}
