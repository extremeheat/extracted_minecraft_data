package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TallTaigaTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState field_181645_a;
   private static final IBlockState field_181646_b;

   public TallTaigaTreeFeature(boolean var1) {
      super(var1);
   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      int var5 = var3.nextInt(4) + 6;
      int var6 = 1 + var3.nextInt(2);
      int var7 = var5 - var6;
      int var8 = 2 + var3.nextInt(2);
      boolean var9 = true;
      if (var4.func_177956_o() >= 1 && var4.func_177956_o() + var5 + 1 <= 256) {
         int var11;
         int var14;
         for(int var10 = var4.func_177956_o(); var10 <= var4.func_177956_o() + 1 + var5 && var9; ++var10) {
            if (var10 - var4.func_177956_o() < var6) {
               var11 = 0;
            } else {
               var11 = var8;
            }

            BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

            for(int var13 = var4.func_177958_n() - var11; var13 <= var4.func_177958_n() + var11 && var9; ++var13) {
               for(var14 = var4.func_177952_p() - var11; var14 <= var4.func_177952_p() + var11 && var9; ++var14) {
                  if (var10 >= 0 && var10 < 256) {
                     IBlockState var15 = var2.func_180495_p(var12.func_181079_c(var13, var10, var14));
                     if (!var15.func_196958_f() && !var15.func_203425_a(BlockTags.field_206952_E)) {
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
            Block var21 = var2.func_180495_p(var4.func_177977_b()).func_177230_c();
            if ((var21 == Blocks.field_196658_i || Block.func_196245_f(var21) || var21 == Blocks.field_150458_ak) && var4.func_177956_o() < 256 - var5 - 1) {
               this.func_175921_a(var2, var4.func_177977_b());
               var11 = var3.nextInt(2);
               int var22 = 1;
               byte var23 = 0;

               int var24;
               for(var14 = 0; var14 <= var7; ++var14) {
                  var24 = var4.func_177956_o() + var5 - var14;

                  for(int var16 = var4.func_177958_n() - var11; var16 <= var4.func_177958_n() + var11; ++var16) {
                     int var17 = var16 - var4.func_177958_n();

                     for(int var18 = var4.func_177952_p() - var11; var18 <= var4.func_177952_p() + var11; ++var18) {
                        int var19 = var18 - var4.func_177952_p();
                        if (Math.abs(var17) != var11 || Math.abs(var19) != var11 || var11 <= 0) {
                           BlockPos var20 = new BlockPos(var16, var24, var18);
                           if (!var2.func_180495_p(var20).func_200015_d(var2, var20)) {
                              this.func_202278_a(var2, var20, field_181646_b);
                           }
                        }
                     }
                  }

                  if (var11 >= var22) {
                     var11 = var23;
                     var23 = 1;
                     ++var22;
                     if (var22 > var8) {
                        var22 = var8;
                     }
                  } else {
                     ++var11;
                  }
               }

               var14 = var3.nextInt(3);

               for(var24 = 0; var24 < var5 - var14; ++var24) {
                  IBlockState var25 = var2.func_180495_p(var4.func_177981_b(var24));
                  if (var25.func_196958_f() || var25.func_203425_a(BlockTags.field_206952_E)) {
                     this.func_208520_a(var1, var2, var4.func_177981_b(var24), field_181645_a);
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
      field_181645_a = Blocks.field_196618_L.func_176223_P();
      field_181646_b = Blocks.field_196645_X.func_176223_P();
   }
}
