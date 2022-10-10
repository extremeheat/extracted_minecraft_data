package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState field_181653_a;
   private static final IBlockState field_181654_b;
   protected final int field_76533_a;
   private final boolean field_76531_b;
   private final IBlockState field_76532_c;
   private final IBlockState field_76530_d;

   public TreeFeature(boolean var1) {
      this(var1, 4, field_181653_a, field_181654_b, false);
   }

   public TreeFeature(boolean var1, int var2, IBlockState var3, IBlockState var4, boolean var5) {
      super(var1);
      this.field_76533_a = var2;
      this.field_76532_c = var3;
      this.field_76530_d = var4;
      this.field_76531_b = var5;
   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      int var5 = this.func_208534_a(var3);
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
            Block var20 = var2.func_180495_p(var4.func_177977_b()).func_177230_c();
            if ((var20 == Blocks.field_196658_i || Block.func_196245_f(var20) || var20 == Blocks.field_150458_ak) && var4.func_177956_o() < 256 - var5 - 1) {
               this.func_175921_a(var2, var4.func_177977_b());
               boolean var21 = true;
               boolean var22 = false;

               int var12;
               int var14;
               int var15;
               BlockPos var17;
               for(var10 = var4.func_177956_o() - 3 + var5; var10 <= var4.func_177956_o() + var5; ++var10) {
                  var11 = var10 - (var4.func_177956_o() + var5);
                  var12 = 1 - var11 / 2;

                  for(int var13 = var4.func_177958_n() - var12; var13 <= var4.func_177958_n() + var12; ++var13) {
                     var14 = var13 - var4.func_177958_n();

                     for(var15 = var4.func_177952_p() - var12; var15 <= var4.func_177952_p() + var12; ++var15) {
                        int var16 = var15 - var4.func_177952_p();
                        if (Math.abs(var14) != var12 || Math.abs(var16) != var12 || var3.nextInt(2) != 0 && var11 != 0) {
                           var17 = new BlockPos(var13, var10, var15);
                           IBlockState var18 = var2.func_180495_p(var17);
                           Material var19 = var18.func_185904_a();
                           if (var18.func_196958_f() || var18.func_203425_a(BlockTags.field_206952_E) || var19 == Material.field_151582_l) {
                              this.func_202278_a(var2, var17, this.field_76530_d);
                           }
                        }
                     }
                  }
               }

               for(var10 = 0; var10 < var5; ++var10) {
                  IBlockState var23 = var2.func_180495_p(var4.func_177981_b(var10));
                  Material var24 = var23.func_185904_a();
                  if (var23.func_196958_f() || var23.func_203425_a(BlockTags.field_206952_E) || var24 == Material.field_151582_l) {
                     this.func_208520_a(var1, var2, var4.func_177981_b(var10), this.field_76532_c);
                     if (this.field_76531_b && var10 > 0) {
                        if (var3.nextInt(3) > 0 && var2.func_175623_d(var4.func_177982_a(-1, var10, 0))) {
                           this.func_181651_a(var2, var4.func_177982_a(-1, var10, 0), BlockVine.field_176278_M);
                        }

                        if (var3.nextInt(3) > 0 && var2.func_175623_d(var4.func_177982_a(1, var10, 0))) {
                           this.func_181651_a(var2, var4.func_177982_a(1, var10, 0), BlockVine.field_176280_O);
                        }

                        if (var3.nextInt(3) > 0 && var2.func_175623_d(var4.func_177982_a(0, var10, -1))) {
                           this.func_181651_a(var2, var4.func_177982_a(0, var10, -1), BlockVine.field_176279_N);
                        }

                        if (var3.nextInt(3) > 0 && var2.func_175623_d(var4.func_177982_a(0, var10, 1))) {
                           this.func_181651_a(var2, var4.func_177982_a(0, var10, 1), BlockVine.field_176273_b);
                        }
                     }
                  }
               }

               if (this.field_76531_b) {
                  for(var10 = var4.func_177956_o() - 3 + var5; var10 <= var4.func_177956_o() + var5; ++var10) {
                     var11 = var10 - (var4.func_177956_o() + var5);
                     var12 = 2 - var11 / 2;
                     BlockPos.MutableBlockPos var27 = new BlockPos.MutableBlockPos();

                     for(var14 = var4.func_177958_n() - var12; var14 <= var4.func_177958_n() + var12; ++var14) {
                        for(var15 = var4.func_177952_p() - var12; var15 <= var4.func_177952_p() + var12; ++var15) {
                           var27.func_181079_c(var14, var10, var15);
                           if (var2.func_180495_p(var27).func_203425_a(BlockTags.field_206952_E)) {
                              BlockPos var29 = var27.func_177976_e();
                              var17 = var27.func_177974_f();
                              BlockPos var30 = var27.func_177978_c();
                              BlockPos var31 = var27.func_177968_d();
                              if (var3.nextInt(4) == 0 && var2.func_180495_p(var29).func_196958_f()) {
                                 this.func_181650_b(var2, var29, BlockVine.field_176278_M);
                              }

                              if (var3.nextInt(4) == 0 && var2.func_180495_p(var17).func_196958_f()) {
                                 this.func_181650_b(var2, var17, BlockVine.field_176280_O);
                              }

                              if (var3.nextInt(4) == 0 && var2.func_180495_p(var30).func_196958_f()) {
                                 this.func_181650_b(var2, var30, BlockVine.field_176279_N);
                              }

                              if (var3.nextInt(4) == 0 && var2.func_180495_p(var31).func_196958_f()) {
                                 this.func_181650_b(var2, var31, BlockVine.field_176273_b);
                              }
                           }
                        }
                     }
                  }

                  if (var3.nextInt(5) == 0 && var5 > 5) {
                     for(var10 = 0; var10 < 2; ++var10) {
                        Iterator var25 = EnumFacing.Plane.HORIZONTAL.iterator();

                        while(var25.hasNext()) {
                           EnumFacing var26 = (EnumFacing)var25.next();
                           if (var3.nextInt(4 - var10) == 0) {
                              EnumFacing var28 = var26.func_176734_d();
                              this.func_181652_a(var2, var3.nextInt(3), var4.func_177982_a(var28.func_82601_c(), var5 - 5 + var10, var28.func_82599_e()), var26);
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

   protected int func_208534_a(Random var1) {
      return this.field_76533_a + var1.nextInt(3);
   }

   private void func_181652_a(IWorld var1, int var2, BlockPos var3, EnumFacing var4) {
      this.func_202278_a(var1, var3, (IBlockState)((IBlockState)Blocks.field_150375_by.func_176223_P().func_206870_a(BlockCocoa.field_176501_a, var2)).func_206870_a(BlockCocoa.field_185512_D, var4));
   }

   private void func_181651_a(IWorld var1, BlockPos var2, BooleanProperty var3) {
      this.func_202278_a(var1, var2, (IBlockState)Blocks.field_150395_bd.func_176223_P().func_206870_a(var3, true));
   }

   private void func_181650_b(IWorld var1, BlockPos var2, BooleanProperty var3) {
      this.func_181651_a(var1, var2, var3);
      int var4 = 4;

      for(var2 = var2.func_177977_b(); var1.func_180495_p(var2).func_196958_f() && var4 > 0; --var4) {
         this.func_181651_a(var1, var2, var3);
         var2 = var2.func_177977_b();
      }

   }

   static {
      field_181653_a = Blocks.field_196617_K.func_176223_P();
      field_181654_b = Blocks.field_196642_W.func_176223_P();
   }
}
