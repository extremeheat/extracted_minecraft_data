package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenTaiga2 extends WorldGenAbstractTree {
   private static final IBlockState field_181645_a;
   private static final IBlockState field_181646_b;

   public WorldGenTaiga2(boolean var1) {
      super(var1);
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      int var4 = var2.nextInt(4) + 6;
      int var5 = 1 + var2.nextInt(2);
      int var6 = var4 - var5;
      int var7 = 2 + var2.nextInt(2);
      boolean var8 = true;
      if (var3.func_177956_o() >= 1 && var3.func_177956_o() + var4 + 1 <= 256) {
         int var13;
         int var21;
         for(int var9 = var3.func_177956_o(); var9 <= var3.func_177956_o() + 1 + var4 && var8; ++var9) {
            boolean var10 = true;
            if (var9 - var3.func_177956_o() < var5) {
               var21 = 0;
            } else {
               var21 = var7;
            }

            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

            for(int var12 = var3.func_177958_n() - var21; var12 <= var3.func_177958_n() + var21 && var8; ++var12) {
               for(var13 = var3.func_177952_p() - var21; var13 <= var3.func_177952_p() + var21 && var8; ++var13) {
                  if (var9 >= 0 && var9 < 256) {
                     Block var14 = var1.func_180495_p(var11.func_181079_c(var12, var9, var13)).func_177230_c();
                     if (var14.func_149688_o() != Material.field_151579_a && var14.func_149688_o() != Material.field_151584_j) {
                        var8 = false;
                     }
                  } else {
                     var8 = false;
                  }
               }
            }
         }

         if (!var8) {
            return false;
         } else {
            Block var20 = var1.func_180495_p(var3.func_177977_b()).func_177230_c();
            if ((var20 == Blocks.field_150349_c || var20 == Blocks.field_150346_d || var20 == Blocks.field_150458_ak) && var3.func_177956_o() < 256 - var4 - 1) {
               this.func_175921_a(var1, var3.func_177977_b());
               var21 = var2.nextInt(2);
               int var22 = 1;
               byte var23 = 0;

               int var24;
               for(var13 = 0; var13 <= var6; ++var13) {
                  var24 = var3.func_177956_o() + var4 - var13;

                  for(int var15 = var3.func_177958_n() - var21; var15 <= var3.func_177958_n() + var21; ++var15) {
                     int var16 = var15 - var3.func_177958_n();

                     for(int var17 = var3.func_177952_p() - var21; var17 <= var3.func_177952_p() + var21; ++var17) {
                        int var18 = var17 - var3.func_177952_p();
                        if (Math.abs(var16) != var21 || Math.abs(var18) != var21 || var21 <= 0) {
                           BlockPos var19 = new BlockPos(var15, var24, var17);
                           if (!var1.func_180495_p(var19).func_177230_c().func_149730_j()) {
                              this.func_175903_a(var1, var19, field_181646_b);
                           }
                        }
                     }
                  }

                  if (var21 >= var22) {
                     var21 = var23;
                     var23 = 1;
                     ++var22;
                     if (var22 > var7) {
                        var22 = var7;
                     }
                  } else {
                     ++var21;
                  }
               }

               var13 = var2.nextInt(3);

               for(var24 = 0; var24 < var4 - var13; ++var24) {
                  Block var25 = var1.func_180495_p(var3.func_177981_b(var24)).func_177230_c();
                  if (var25.func_149688_o() == Material.field_151579_a || var25.func_149688_o() == Material.field_151584_j) {
                     this.func_175903_a(var1, var3.func_177981_b(var24), field_181645_a);
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

   static {
      field_181645_a = Blocks.field_150364_r.func_176223_P().func_177226_a(BlockOldLog.field_176301_b, BlockPlanks.EnumType.SPRUCE);
      field_181646_b = Blocks.field_150362_t.func_176223_P().func_177226_a(BlockOldLeaf.field_176239_P, BlockPlanks.EnumType.SPRUCE).func_177226_a(BlockLeaves.field_176236_b, false);
   }
}
