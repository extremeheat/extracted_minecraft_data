package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SwampTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState field_181648_a;
   private static final IBlockState field_181649_b;

   public SwampTreeFeature() {
      super(false);
   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      int var5;
      for(var5 = var3.nextInt(4) + 5; var2.func_204610_c(var4.func_177977_b()).func_206884_a(FluidTags.field_206959_a); var4 = var4.func_177977_b()) {
      }

      boolean var6 = true;
      if (var4.func_177956_o() >= 1 && var4.func_177956_o() + var5 + 1 <= 256) {
         int var10;
         int var11;
         for(int var7 = var4.func_177956_o(); var7 <= var4.func_177956_o() + 1 + var5; ++var7) {
            byte var8 = 1;
            if (var7 == var4.func_177956_o()) {
               var8 = 0;
            }

            if (var7 >= var4.func_177956_o() + 1 + var5 - 2) {
               var8 = 3;
            }

            BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

            for(var10 = var4.func_177958_n() - var8; var10 <= var4.func_177958_n() + var8 && var6; ++var10) {
               for(var11 = var4.func_177952_p() - var8; var11 <= var4.func_177952_p() + var8 && var6; ++var11) {
                  if (var7 >= 0 && var7 < 256) {
                     IBlockState var12 = var2.func_180495_p(var9.func_181079_c(var10, var7, var11));
                     Block var13 = var12.func_177230_c();
                     if (!var12.func_196958_f() && !var12.func_203425_a(BlockTags.field_206952_E)) {
                        if (var13 == Blocks.field_150355_j) {
                           if (var7 > var4.func_177956_o()) {
                              var6 = false;
                           }
                        } else {
                           var6 = false;
                        }
                     }
                  } else {
                     var6 = false;
                  }
               }
            }
         }

         if (!var6) {
            return false;
         } else {
            Block var18 = var2.func_180495_p(var4.func_177977_b()).func_177230_c();
            if ((var18 == Blocks.field_196658_i || Block.func_196245_f(var18)) && var4.func_177956_o() < 256 - var5 - 1) {
               this.func_175921_a(var2, var4.func_177977_b());

               BlockPos var15;
               int var19;
               int var20;
               int var23;
               int var25;
               for(var19 = var4.func_177956_o() - 3 + var5; var19 <= var4.func_177956_o() + var5; ++var19) {
                  var20 = var19 - (var4.func_177956_o() + var5);
                  var10 = 2 - var20 / 2;

                  for(var11 = var4.func_177958_n() - var10; var11 <= var4.func_177958_n() + var10; ++var11) {
                     var23 = var11 - var4.func_177958_n();

                     for(var25 = var4.func_177952_p() - var10; var25 <= var4.func_177952_p() + var10; ++var25) {
                        int var14 = var25 - var4.func_177952_p();
                        if (Math.abs(var23) != var10 || Math.abs(var14) != var10 || var3.nextInt(2) != 0 && var20 != 0) {
                           var15 = new BlockPos(var11, var19, var25);
                           if (!var2.func_180495_p(var15).func_200015_d(var2, var15)) {
                              this.func_202278_a(var2, var15, field_181649_b);
                           }
                        }
                     }
                  }
               }

               for(var19 = 0; var19 < var5; ++var19) {
                  IBlockState var21 = var2.func_180495_p(var4.func_177981_b(var19));
                  Block var22 = var21.func_177230_c();
                  if (var21.func_196958_f() || var21.func_203425_a(BlockTags.field_206952_E) || var22 == Blocks.field_150355_j) {
                     this.func_208520_a(var1, var2, var4.func_177981_b(var19), field_181648_a);
                  }
               }

               for(var19 = var4.func_177956_o() - 3 + var5; var19 <= var4.func_177956_o() + var5; ++var19) {
                  var20 = var19 - (var4.func_177956_o() + var5);
                  var10 = 2 - var20 / 2;
                  BlockPos.MutableBlockPos var24 = new BlockPos.MutableBlockPos();

                  for(var23 = var4.func_177958_n() - var10; var23 <= var4.func_177958_n() + var10; ++var23) {
                     for(var25 = var4.func_177952_p() - var10; var25 <= var4.func_177952_p() + var10; ++var25) {
                        var24.func_181079_c(var23, var19, var25);
                        if (var2.func_180495_p(var24).func_203425_a(BlockTags.field_206952_E)) {
                           BlockPos var26 = var24.func_177976_e();
                           var15 = var24.func_177974_f();
                           BlockPos var16 = var24.func_177978_c();
                           BlockPos var17 = var24.func_177968_d();
                           if (var3.nextInt(4) == 0 && var2.func_180495_p(var26).func_196958_f()) {
                              this.func_181647_a(var2, var26, BlockVine.field_176278_M);
                           }

                           if (var3.nextInt(4) == 0 && var2.func_180495_p(var15).func_196958_f()) {
                              this.func_181647_a(var2, var15, BlockVine.field_176280_O);
                           }

                           if (var3.nextInt(4) == 0 && var2.func_180495_p(var16).func_196958_f()) {
                              this.func_181647_a(var2, var16, BlockVine.field_176279_N);
                           }

                           if (var3.nextInt(4) == 0 && var2.func_180495_p(var17).func_196958_f()) {
                              this.func_181647_a(var2, var17, BlockVine.field_176273_b);
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

   private void func_181647_a(IWorld var1, BlockPos var2, BooleanProperty var3) {
      IBlockState var4 = (IBlockState)Blocks.field_150395_bd.func_176223_P().func_206870_a(var3, true);
      this.func_202278_a(var1, var2, var4);
      int var5 = 4;

      for(var2 = var2.func_177977_b(); var1.func_180495_p(var2).func_196958_f() && var5 > 0; --var5) {
         this.func_202278_a(var1, var2, var4);
         var2 = var2.func_177977_b();
      }

   }

   static {
      field_181648_a = Blocks.field_196617_K.func_176223_P();
      field_181649_b = Blocks.field_196642_W.func_176223_P();
   }
}
