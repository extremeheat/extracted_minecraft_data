package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class PointyTaigaTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState field_181636_a;
   private static final IBlockState field_181637_b;

   public PointyTaigaTreeFeature() {
      super(false);
   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      int var5 = var3.nextInt(5) + 7;
      int var6 = var5 - var3.nextInt(2) - 3;
      int var7 = var5 - var6;
      int var8 = 1 + var3.nextInt(var7 + 1);
      if (var4.func_177956_o() >= 1 && var4.func_177956_o() + var5 + 1 <= 256) {
         boolean var9 = true;

         int var13;
         int var14;
         int var19;
         for(int var10 = var4.func_177956_o(); var10 <= var4.func_177956_o() + 1 + var5 && var9; ++var10) {
            boolean var11 = true;
            if (var10 - var4.func_177956_o() < var6) {
               var19 = 0;
            } else {
               var19 = var8;
            }

            BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

            for(var13 = var4.func_177958_n() - var19; var13 <= var4.func_177958_n() + var19 && var9; ++var13) {
               for(var14 = var4.func_177952_p() - var19; var14 <= var4.func_177952_p() + var19 && var9; ++var14) {
                  if (var10 >= 0 && var10 < 256) {
                     if (!this.func_150523_a(var2.func_180495_p(var12.func_181079_c(var13, var10, var14)).func_177230_c())) {
                        var9 = false;
                     }
                  } else {
                     var9 = false;
                  }
               }
            }
         }

         if (!var9) {
            return false;
         } else {
            Block var18 = var2.func_180495_p(var4.func_177977_b()).func_177230_c();
            if ((var18 == Blocks.field_196658_i || Block.func_196245_f(var18)) && var4.func_177956_o() < 256 - var5 - 1) {
               this.func_175921_a(var2, var4.func_177977_b());
               var19 = 0;

               int var20;
               for(var20 = var4.func_177956_o() + var5; var20 >= var4.func_177956_o() + var6; --var20) {
                  for(var13 = var4.func_177958_n() - var19; var13 <= var4.func_177958_n() + var19; ++var13) {
                     var14 = var13 - var4.func_177958_n();

                     for(int var15 = var4.func_177952_p() - var19; var15 <= var4.func_177952_p() + var19; ++var15) {
                        int var16 = var15 - var4.func_177952_p();
                        if (Math.abs(var14) != var19 || Math.abs(var16) != var19 || var19 <= 0) {
                           BlockPos var17 = new BlockPos(var13, var20, var15);
                           if (!var2.func_180495_p(var17).func_200015_d(var2, var17)) {
                              this.func_202278_a(var2, var17, field_181637_b);
                           }
                        }
                     }
                  }

                  if (var19 >= 1 && var20 == var4.func_177956_o() + var6 + 1) {
                     --var19;
                  } else if (var19 < var8) {
                     ++var19;
                  }
               }

               for(var20 = 0; var20 < var5 - 1; ++var20) {
                  IBlockState var21 = var2.func_180495_p(var4.func_177981_b(var20));
                  if (var21.func_196958_f() || var21.func_203425_a(BlockTags.field_206952_E)) {
                     this.func_208520_a(var1, var2, var4.func_177981_b(var20), field_181636_a);
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
      field_181636_a = Blocks.field_196618_L.func_176223_P();
      field_181637_b = Blocks.field_196645_X.func_176223_P();
   }
}
