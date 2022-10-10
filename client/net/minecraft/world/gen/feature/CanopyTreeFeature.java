package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class CanopyTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState field_181640_a;
   private static final IBlockState field_181641_b;

   public CanopyTreeFeature(boolean var1) {
      super(var1);
   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      int var5 = var3.nextInt(3) + var3.nextInt(2) + 6;
      int var6 = var4.func_177958_n();
      int var7 = var4.func_177956_o();
      int var8 = var4.func_177952_p();
      if (var7 >= 1 && var7 + var5 + 1 < 256) {
         BlockPos var9 = var4.func_177977_b();
         Block var10 = var2.func_180495_p(var9).func_177230_c();
         if (var10 != Blocks.field_196658_i && !Block.func_196245_f(var10)) {
            return false;
         } else if (!this.func_181638_a(var2, var4, var5)) {
            return false;
         } else {
            this.func_175921_a(var2, var9);
            this.func_175921_a(var2, var9.func_177974_f());
            this.func_175921_a(var2, var9.func_177968_d());
            this.func_175921_a(var2, var9.func_177968_d().func_177974_f());
            EnumFacing var11 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var3);
            int var12 = var5 - var3.nextInt(4);
            int var13 = 2 - var3.nextInt(3);
            int var14 = var6;
            int var15 = var8;
            int var16 = var7 + var5 - 1;

            int var17;
            int var18;
            for(var17 = 0; var17 < var5; ++var17) {
               if (var17 >= var12 && var13 > 0) {
                  var14 += var11.func_82601_c();
                  var15 += var11.func_82599_e();
                  --var13;
               }

               var18 = var7 + var17;
               BlockPos var19 = new BlockPos(var14, var18, var15);
               IBlockState var20 = var2.func_180495_p(var19);
               if (var20.func_196958_f() || var20.func_203425_a(BlockTags.field_206952_E)) {
                  this.func_208533_a(var1, var2, var19);
                  this.func_208533_a(var1, var2, var19.func_177974_f());
                  this.func_208533_a(var1, var2, var19.func_177968_d());
                  this.func_208533_a(var1, var2, var19.func_177974_f().func_177968_d());
               }
            }

            for(var17 = -2; var17 <= 0; ++var17) {
               for(var18 = -2; var18 <= 0; ++var18) {
                  byte var22 = -1;
                  this.func_202414_a(var2, var14 + var17, var16 + var22, var15 + var18);
                  this.func_202414_a(var2, 1 + var14 - var17, var16 + var22, var15 + var18);
                  this.func_202414_a(var2, var14 + var17, var16 + var22, 1 + var15 - var18);
                  this.func_202414_a(var2, 1 + var14 - var17, var16 + var22, 1 + var15 - var18);
                  if ((var17 > -2 || var18 > -1) && (var17 != -1 || var18 != -2)) {
                     byte var23 = 1;
                     this.func_202414_a(var2, var14 + var17, var16 + var23, var15 + var18);
                     this.func_202414_a(var2, 1 + var14 - var17, var16 + var23, var15 + var18);
                     this.func_202414_a(var2, var14 + var17, var16 + var23, 1 + var15 - var18);
                     this.func_202414_a(var2, 1 + var14 - var17, var16 + var23, 1 + var15 - var18);
                  }
               }
            }

            if (var3.nextBoolean()) {
               this.func_202414_a(var2, var14, var16 + 2, var15);
               this.func_202414_a(var2, var14 + 1, var16 + 2, var15);
               this.func_202414_a(var2, var14 + 1, var16 + 2, var15 + 1);
               this.func_202414_a(var2, var14, var16 + 2, var15 + 1);
            }

            for(var17 = -3; var17 <= 4; ++var17) {
               for(var18 = -3; var18 <= 4; ++var18) {
                  if ((var17 != -3 || var18 != -3) && (var17 != -3 || var18 != 4) && (var17 != 4 || var18 != -3) && (var17 != 4 || var18 != 4) && (Math.abs(var17) < 3 || Math.abs(var18) < 3)) {
                     this.func_202414_a(var2, var14 + var17, var16, var15 + var18);
                  }
               }
            }

            for(var17 = -1; var17 <= 2; ++var17) {
               for(var18 = -1; var18 <= 2; ++var18) {
                  if ((var17 < 0 || var17 > 1 || var18 < 0 || var18 > 1) && var3.nextInt(3) <= 0) {
                     int var24 = var3.nextInt(3) + 2;

                     int var25;
                     for(var25 = 0; var25 < var24; ++var25) {
                        this.func_208533_a(var1, var2, new BlockPos(var6 + var17, var16 - var25 - 1, var8 + var18));
                     }

                     int var21;
                     for(var25 = -1; var25 <= 1; ++var25) {
                        for(var21 = -1; var21 <= 1; ++var21) {
                           this.func_202414_a(var2, var14 + var17 + var25, var16, var15 + var18 + var21);
                        }
                     }

                     for(var25 = -2; var25 <= 2; ++var25) {
                        for(var21 = -2; var21 <= 2; ++var21) {
                           if (Math.abs(var25) != 2 || Math.abs(var21) != 2) {
                              this.func_202414_a(var2, var14 + var17 + var25, var16 - 1, var15 + var18 + var21);
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

   private boolean func_181638_a(IBlockReader var1, BlockPos var2, int var3) {
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

   private void func_208533_a(Set<BlockPos> var1, IWorld var2, BlockPos var3) {
      if (this.func_150523_a(var2.func_180495_p(var3).func_177230_c())) {
         this.func_208520_a(var1, var2, var3, field_181640_a);
      }

   }

   private void func_202414_a(IWorld var1, int var2, int var3, int var4) {
      BlockPos var5 = new BlockPos(var2, var3, var4);
      if (var1.func_180495_p(var5).func_196958_f()) {
         this.func_202278_a(var1, var5, field_181641_b);
      }

   }

   static {
      field_181640_a = Blocks.field_196623_P.func_176223_P();
      field_181641_b = Blocks.field_196574_ab.func_176223_P();
   }
}
