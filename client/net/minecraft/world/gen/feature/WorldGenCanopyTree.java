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

public class WorldGenCanopyTree extends WorldGenAbstractTree {
   private static final IBlockState field_181640_a;
   private static final IBlockState field_181641_b;

   public WorldGenCanopyTree(boolean var1) {
      super(var1);
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      int var4 = var2.nextInt(3) + var2.nextInt(2) + 6;
      int var5 = var3.func_177958_n();
      int var6 = var3.func_177956_o();
      int var7 = var3.func_177952_p();
      if (var6 >= 1 && var6 + var4 + 1 < 256) {
         BlockPos var8 = var3.func_177977_b();
         Block var9 = var1.func_180495_p(var8).func_177230_c();
         if (var9 != Blocks.field_150349_c && var9 != Blocks.field_150346_d) {
            return false;
         } else if (!this.func_181638_a(var1, var3, var4)) {
            return false;
         } else {
            this.func_175921_a(var1, var8);
            this.func_175921_a(var1, var8.func_177974_f());
            this.func_175921_a(var1, var8.func_177968_d());
            this.func_175921_a(var1, var8.func_177968_d().func_177974_f());
            EnumFacing var10 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var2);
            int var11 = var4 - var2.nextInt(4);
            int var12 = 2 - var2.nextInt(3);
            int var13 = var5;
            int var14 = var7;
            int var15 = var6 + var4 - 1;

            int var16;
            int var17;
            for(var16 = 0; var16 < var4; ++var16) {
               if (var16 >= var11 && var12 > 0) {
                  var13 += var10.func_82601_c();
                  var14 += var10.func_82599_e();
                  --var12;
               }

               var17 = var6 + var16;
               BlockPos var18 = new BlockPos(var13, var17, var14);
               Material var19 = var1.func_180495_p(var18).func_177230_c().func_149688_o();
               if (var19 == Material.field_151579_a || var19 == Material.field_151584_j) {
                  this.func_181639_b(var1, var18);
                  this.func_181639_b(var1, var18.func_177974_f());
                  this.func_181639_b(var1, var18.func_177968_d());
                  this.func_181639_b(var1, var18.func_177974_f().func_177968_d());
               }
            }

            for(var16 = -2; var16 <= 0; ++var16) {
               for(var17 = -2; var17 <= 0; ++var17) {
                  byte var21 = -1;
                  this.func_150526_a(var1, var13 + var16, var15 + var21, var14 + var17);
                  this.func_150526_a(var1, 1 + var13 - var16, var15 + var21, var14 + var17);
                  this.func_150526_a(var1, var13 + var16, var15 + var21, 1 + var14 - var17);
                  this.func_150526_a(var1, 1 + var13 - var16, var15 + var21, 1 + var14 - var17);
                  if ((var16 > -2 || var17 > -1) && (var16 != -1 || var17 != -2)) {
                     byte var22 = 1;
                     this.func_150526_a(var1, var13 + var16, var15 + var22, var14 + var17);
                     this.func_150526_a(var1, 1 + var13 - var16, var15 + var22, var14 + var17);
                     this.func_150526_a(var1, var13 + var16, var15 + var22, 1 + var14 - var17);
                     this.func_150526_a(var1, 1 + var13 - var16, var15 + var22, 1 + var14 - var17);
                  }
               }
            }

            if (var2.nextBoolean()) {
               this.func_150526_a(var1, var13, var15 + 2, var14);
               this.func_150526_a(var1, var13 + 1, var15 + 2, var14);
               this.func_150526_a(var1, var13 + 1, var15 + 2, var14 + 1);
               this.func_150526_a(var1, var13, var15 + 2, var14 + 1);
            }

            for(var16 = -3; var16 <= 4; ++var16) {
               for(var17 = -3; var17 <= 4; ++var17) {
                  if ((var16 != -3 || var17 != -3) && (var16 != -3 || var17 != 4) && (var16 != 4 || var17 != -3) && (var16 != 4 || var17 != 4) && (Math.abs(var16) < 3 || Math.abs(var17) < 3)) {
                     this.func_150526_a(var1, var13 + var16, var15, var14 + var17);
                  }
               }
            }

            for(var16 = -1; var16 <= 2; ++var16) {
               for(var17 = -1; var17 <= 2; ++var17) {
                  if ((var16 < 0 || var16 > 1 || var17 < 0 || var17 > 1) && var2.nextInt(3) <= 0) {
                     int var23 = var2.nextInt(3) + 2;

                     int var24;
                     for(var24 = 0; var24 < var23; ++var24) {
                        this.func_181639_b(var1, new BlockPos(var5 + var16, var15 - var24 - 1, var7 + var17));
                     }

                     int var20;
                     for(var24 = -1; var24 <= 1; ++var24) {
                        for(var20 = -1; var20 <= 1; ++var20) {
                           this.func_150526_a(var1, var13 + var16 + var24, var15, var14 + var17 + var20);
                        }
                     }

                     for(var24 = -2; var24 <= 2; ++var24) {
                        for(var20 = -2; var20 <= 2; ++var20) {
                           if (Math.abs(var24) != 2 || Math.abs(var20) != 2) {
                              this.func_150526_a(var1, var13 + var16 + var24, var15 - 1, var14 + var17 + var20);
                           }
                        }
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean func_181638_a(World var1, BlockPos var2, int var3) {
      int var4 = var2.func_177958_n();
      int var5 = var2.func_177956_o();
      int var6 = var2.func_177952_p();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for(int var8 = 0; var8 <= var3 + 1; ++var8) {
         byte var9 = 1;
         if (var8 == 0) {
            var9 = 0;
         }

         if (var8 >= var3 - 1) {
            var9 = 2;
         }

         for(int var10 = -var9; var10 <= var9; ++var10) {
            for(int var11 = -var9; var11 <= var9; ++var11) {
               if (!this.func_150523_a(var1.func_180495_p(var7.func_181079_c(var4 + var10, var5 + var8, var6 + var11)).func_177230_c())) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void func_181639_b(World var1, BlockPos var2) {
      if (this.func_150523_a(var1.func_180495_p(var2).func_177230_c())) {
         this.func_175903_a(var1, var2, field_181640_a);
      }

   }

   private void func_150526_a(World var1, int var2, int var3, int var4) {
      BlockPos var5 = new BlockPos(var2, var3, var4);
      Block var6 = var1.func_180495_p(var5).func_177230_c();
      if (var6.func_149688_o() == Material.field_151579_a) {
         this.func_175903_a(var1, var5, field_181641_b);
      }

   }

   static {
      field_181640_a = Blocks.field_150363_s.func_176223_P().func_177226_a(BlockNewLog.field_176300_b, BlockPlanks.EnumType.DARK_OAK);
      field_181641_b = Blocks.field_150361_u.func_176223_P().func_177226_a(BlockNewLeaf.field_176240_P, BlockPlanks.EnumType.DARK_OAK).func_177226_a(BlockLeaves.field_176236_b, false);
   }
}
