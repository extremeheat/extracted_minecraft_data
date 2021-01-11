package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenAbstractTree {
   private static final IBlockState field_181629_a;
   private static final IBlockState field_181630_b;
   private boolean field_150531_a;

   public WorldGenForest(boolean var1, boolean var2) {
      super(var1);
      this.field_150531_a = var2;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      int var4 = var2.nextInt(3) + 5;
      if (this.field_150531_a) {
         var4 += var2.nextInt(7);
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
            Block var16 = var1.func_180495_p(var3.func_177977_b()).func_177230_c();
            if ((var16 == Blocks.field_150349_c || var16 == Blocks.field_150346_d || var16 == Blocks.field_150458_ak) && var3.func_177956_o() < 256 - var4 - 1) {
               this.func_175921_a(var1, var3.func_177977_b());

               int var17;
               for(var17 = var3.func_177956_o() - 3 + var4; var17 <= var3.func_177956_o() + var4; ++var17) {
                  int var18 = var17 - (var3.func_177956_o() + var4);
                  var9 = 1 - var18 / 2;

                  for(var10 = var3.func_177958_n() - var9; var10 <= var3.func_177958_n() + var9; ++var10) {
                     int var11 = var10 - var3.func_177958_n();

                     for(int var12 = var3.func_177952_p() - var9; var12 <= var3.func_177952_p() + var9; ++var12) {
                        int var13 = var12 - var3.func_177952_p();
                        if (Math.abs(var11) != var9 || Math.abs(var13) != var9 || var2.nextInt(2) != 0 && var18 != 0) {
                           BlockPos var14 = new BlockPos(var10, var17, var12);
                           Block var15 = var1.func_180495_p(var14).func_177230_c();
                           if (var15.func_149688_o() == Material.field_151579_a || var15.func_149688_o() == Material.field_151584_j) {
                              this.func_175903_a(var1, var14, field_181630_b);
                           }
                        }
                     }
                  }
               }

               for(var17 = 0; var17 < var4; ++var17) {
                  Block var19 = var1.func_180495_p(var3.func_177981_b(var17)).func_177230_c();
                  if (var19.func_149688_o() == Material.field_151579_a || var19.func_149688_o() == Material.field_151584_j) {
                     this.func_175903_a(var1, var3.func_177981_b(var17), field_181629_a);
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
      field_181629_a = Blocks.field_150364_r.func_176223_P().func_177226_a(BlockOldLog.field_176301_b, BlockPlanks.EnumType.BIRCH);
      field_181630_b = Blocks.field_150362_t.func_176223_P().func_177226_a(BlockOldLeaf.field_176239_P, BlockPlanks.EnumType.BIRCH).func_177226_a(BlockOldLeaf.field_176236_b, false);
   }
}
