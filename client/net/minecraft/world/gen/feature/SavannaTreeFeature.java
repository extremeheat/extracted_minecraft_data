package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SavannaTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState field_181643_a;
   private static final IBlockState field_181644_b;

   public SavannaTreeFeature(boolean var1) {
      super(var1);
   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      int var5 = var3.nextInt(3) + var3.nextInt(3) + 5;
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
               var8 = 2;
            }

            BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

            for(var10 = var4.func_177958_n() - var8; var10 <= var4.func_177958_n() + var8 && var6; ++var10) {
               for(var11 = var4.func_177952_p() - var8; var11 <= var4.func_177952_p() + var8 && var6; ++var11) {
                  if (var7 >= 0 && var7 < 256) {
                     if (!this.func_150523_a(var2.func_180495_p(var9.func_181079_c(var10, var7, var11)).func_177230_c())) {
                        var6 = false;
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
            Block var21 = var2.func_180495_p(var4.func_177977_b()).func_177230_c();
            if ((var21 == Blocks.field_196658_i || Block.func_196245_f(var21)) && var4.func_177956_o() < 256 - var5 - 1) {
               this.func_175921_a(var2, var4.func_177977_b());
               EnumFacing var22 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var3);
               int var23 = var5 - var3.nextInt(4) - 1;
               var10 = 3 - var3.nextInt(3);
               var11 = var4.func_177958_n();
               int var12 = var4.func_177952_p();
               int var13 = 0;

               int var15;
               for(int var14 = 0; var14 < var5; ++var14) {
                  var15 = var4.func_177956_o() + var14;
                  if (var14 >= var23 && var10 > 0) {
                     var11 += var22.func_82601_c();
                     var12 += var22.func_82599_e();
                     --var10;
                  }

                  BlockPos var16 = new BlockPos(var11, var15, var12);
                  IBlockState var17 = var2.func_180495_p(var16);
                  if (var17.func_196958_f() || var17.func_203425_a(BlockTags.field_206952_E)) {
                     this.func_208532_a(var1, var2, var16);
                     var13 = var15;
                  }
               }

               BlockPos var24 = new BlockPos(var11, var13, var12);

               int var26;
               for(var15 = -3; var15 <= 3; ++var15) {
                  for(var26 = -3; var26 <= 3; ++var26) {
                     if (Math.abs(var15) != 3 || Math.abs(var26) != 3) {
                        this.func_175924_b(var2, var24.func_177982_a(var15, 0, var26));
                     }
                  }
               }

               var24 = var24.func_177984_a();

               for(var15 = -1; var15 <= 1; ++var15) {
                  for(var26 = -1; var26 <= 1; ++var26) {
                     this.func_175924_b(var2, var24.func_177982_a(var15, 0, var26));
                  }
               }

               this.func_175924_b(var2, var24.func_177965_g(2));
               this.func_175924_b(var2, var24.func_177985_f(2));
               this.func_175924_b(var2, var24.func_177970_e(2));
               this.func_175924_b(var2, var24.func_177964_d(2));
               var11 = var4.func_177958_n();
               var12 = var4.func_177952_p();
               EnumFacing var25 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var3);
               if (var25 != var22) {
                  var15 = var23 - var3.nextInt(2) - 1;
                  var26 = 1 + var3.nextInt(3);
                  var13 = 0;

                  int var18;
                  for(int var27 = var15; var27 < var5 && var26 > 0; --var26) {
                     if (var27 >= 1) {
                        var18 = var4.func_177956_o() + var27;
                        var11 += var25.func_82601_c();
                        var12 += var25.func_82599_e();
                        BlockPos var19 = new BlockPos(var11, var18, var12);
                        IBlockState var20 = var2.func_180495_p(var19);
                        if (var20.func_196958_f() || var20.func_203425_a(BlockTags.field_206952_E)) {
                           this.func_208532_a(var1, var2, var19);
                           var13 = var18;
                        }
                     }

                     ++var27;
                  }

                  if (var13 > 0) {
                     BlockPos var28 = new BlockPos(var11, var13, var12);

                     int var29;
                     for(var18 = -2; var18 <= 2; ++var18) {
                        for(var29 = -2; var29 <= 2; ++var29) {
                           if (Math.abs(var18) != 2 || Math.abs(var29) != 2) {
                              this.func_175924_b(var2, var28.func_177982_a(var18, 0, var29));
                           }
                        }
                     }

                     var28 = var28.func_177984_a();

                     for(var18 = -1; var18 <= 1; ++var18) {
                        for(var29 = -1; var29 <= 1; ++var29) {
                           this.func_175924_b(var2, var28.func_177982_a(var18, 0, var29));
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

   private void func_208532_a(Set<BlockPos> var1, IWorld var2, BlockPos var3) {
      this.func_208520_a(var1, var2, var3, field_181643_a);
   }

   private void func_175924_b(IWorld var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      if (var3.func_196958_f() || var3.func_203425_a(BlockTags.field_206952_E)) {
         this.func_202278_a(var1, var2, field_181644_b);
      }

   }

   static {
      field_181643_a = Blocks.field_196621_O.func_176223_P();
      field_181644_b = Blocks.field_196572_aa.func_176223_P();
   }
}
