package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSwamp extends WorldGenAbstractTree {
   private static final IBlockState field_181648_a;
   private static final IBlockState field_181649_b;

   public WorldGenSwamp() {
      super(false);
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      int var4;
      for(var4 = var2.nextInt(4) + 5; var1.func_180495_p(var3.func_177977_b()).func_177230_c().func_149688_o() == Material.field_151586_h; var3 = var3.func_177977_b()) {
      }

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
               var7 = 3;
            }

            BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

            for(var9 = var3.func_177958_n() - var7; var9 <= var3.func_177958_n() + var7 && var5; ++var9) {
               for(var10 = var3.func_177952_p() - var7; var10 <= var3.func_177952_p() + var7 && var5; ++var10) {
                  if (var6 >= 0 && var6 < 256) {
                     Block var11 = var1.func_180495_p(var8.func_181079_c(var9, var6, var10)).func_177230_c();
                     if (var11.func_149688_o() != Material.field_151579_a && var11.func_149688_o() != Material.field_151584_j) {
                        if (var11 != Blocks.field_150355_j && var11 != Blocks.field_150358_i) {
                           var5 = false;
                        } else if (var6 > var3.func_177956_o()) {
                           var5 = false;
                        }
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
            Block var17 = var1.func_180495_p(var3.func_177977_b()).func_177230_c();
            if ((var17 == Blocks.field_150349_c || var17 == Blocks.field_150346_d) && var3.func_177956_o() < 256 - var4 - 1) {
               this.func_175921_a(var1, var3.func_177977_b());

               int var12;
               BlockPos var14;
               int var18;
               int var19;
               int var21;
               for(var18 = var3.func_177956_o() - 3 + var4; var18 <= var3.func_177956_o() + var4; ++var18) {
                  var19 = var18 - (var3.func_177956_o() + var4);
                  var9 = 2 - var19 / 2;

                  for(var10 = var3.func_177958_n() - var9; var10 <= var3.func_177958_n() + var9; ++var10) {
                     var21 = var10 - var3.func_177958_n();

                     for(var12 = var3.func_177952_p() - var9; var12 <= var3.func_177952_p() + var9; ++var12) {
                        int var13 = var12 - var3.func_177952_p();
                        if (Math.abs(var21) != var9 || Math.abs(var13) != var9 || var2.nextInt(2) != 0 && var19 != 0) {
                           var14 = new BlockPos(var10, var18, var12);
                           if (!var1.func_180495_p(var14).func_177230_c().func_149730_j()) {
                              this.func_175903_a(var1, var14, field_181649_b);
                           }
                        }
                     }
                  }
               }

               for(var18 = 0; var18 < var4; ++var18) {
                  Block var20 = var1.func_180495_p(var3.func_177981_b(var18)).func_177230_c();
                  if (var20.func_149688_o() == Material.field_151579_a || var20.func_149688_o() == Material.field_151584_j || var20 == Blocks.field_150358_i || var20 == Blocks.field_150355_j) {
                     this.func_175903_a(var1, var3.func_177981_b(var18), field_181648_a);
                  }
               }

               for(var18 = var3.func_177956_o() - 3 + var4; var18 <= var3.func_177956_o() + var4; ++var18) {
                  var19 = var18 - (var3.func_177956_o() + var4);
                  var9 = 2 - var19 / 2;
                  BlockPos.MutableBlockPos var22 = new BlockPos.MutableBlockPos();

                  for(var21 = var3.func_177958_n() - var9; var21 <= var3.func_177958_n() + var9; ++var21) {
                     for(var12 = var3.func_177952_p() - var9; var12 <= var3.func_177952_p() + var9; ++var12) {
                        var22.func_181079_c(var21, var18, var12);
                        if (var1.func_180495_p(var22).func_177230_c().func_149688_o() == Material.field_151584_j) {
                           BlockPos var23 = var22.func_177976_e();
                           var14 = var22.func_177974_f();
                           BlockPos var15 = var22.func_177978_c();
                           BlockPos var16 = var22.func_177968_d();
                           if (var2.nextInt(4) == 0 && var1.func_180495_p(var23).func_177230_c().func_149688_o() == Material.field_151579_a) {
                              this.func_181647_a(var1, var23, BlockVine.field_176278_M);
                           }

                           if (var2.nextInt(4) == 0 && var1.func_180495_p(var14).func_177230_c().func_149688_o() == Material.field_151579_a) {
                              this.func_181647_a(var1, var14, BlockVine.field_176280_O);
                           }

                           if (var2.nextInt(4) == 0 && var1.func_180495_p(var15).func_177230_c().func_149688_o() == Material.field_151579_a) {
                              this.func_181647_a(var1, var15, BlockVine.field_176279_N);
                           }

                           if (var2.nextInt(4) == 0 && var1.func_180495_p(var16).func_177230_c().func_149688_o() == Material.field_151579_a) {
                              this.func_181647_a(var1, var16, BlockVine.field_176273_b);
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

   private void func_181647_a(World var1, BlockPos var2, PropertyBool var3) {
      IBlockState var4 = Blocks.field_150395_bd.func_176223_P().func_177226_a(var3, true);
      this.func_175903_a(var1, var2, var4);
      int var5 = 4;

      for(var2 = var2.func_177977_b(); var1.func_180495_p(var2).func_177230_c().func_149688_o() == Material.field_151579_a && var5 > 0; --var5) {
         this.func_175903_a(var1, var2, var4);
         var2 = var2.func_177977_b();
      }

   }

   static {
      field_181648_a = Blocks.field_150364_r.func_176223_P().func_177226_a(BlockOldLog.field_176301_b, BlockPlanks.EnumType.OAK);
      field_181649_b = Blocks.field_150362_t.func_176223_P().func_177226_a(BlockOldLeaf.field_176239_P, BlockPlanks.EnumType.OAK).func_177226_a(BlockOldLeaf.field_176236_b, false);
   }
}
