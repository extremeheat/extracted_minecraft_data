package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class WorldGenSavannaTree extends WorldGenAbstractTree {
   public WorldGenSavannaTree(boolean var1) {
      super(var1);
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = var2.nextInt(3) + var2.nextInt(3) + 5;
      boolean var7 = true;
      if (var4 >= 1 && var4 + var6 + 1 <= 256) {
         for(int var8 = var4; var8 <= var4 + 1 + var6; ++var8) {
            byte var9 = 1;
            if (var8 == var4) {
               var9 = 0;
            }

            if (var8 >= var4 + 1 + var6 - 2) {
               var9 = 2;
            }

            for(int var10 = var3 - var9; var10 <= var3 + var9 && var7; ++var10) {
               for(int var11 = var5 - var9; var11 <= var5 + var9 && var7; ++var11) {
                  if (var8 >= 0 && var8 < 256) {
                     Block var12 = var1.func_147439_a(var10, var8, var11);
                     if (!this.func_150523_a(var12)) {
                        var7 = false;
                     }
                  } else {
                     var7 = false;
                  }
               }
            }
         }

         if (!var7) {
            return false;
         } else {
            Block var21 = var1.func_147439_a(var3, var4 - 1, var5);
            if ((var21 == Blocks.field_150349_c || var21 == Blocks.field_150346_d) && var4 < 256 - var6 - 1) {
               this.func_150515_a(var1, var3, var4 - 1, var5, Blocks.field_150346_d);
               int var22 = var2.nextInt(4);
               int var23 = var6 - var2.nextInt(4) - 1;
               int var24 = 3 - var2.nextInt(3);
               int var25 = var3;
               int var13 = var5;
               int var14 = 0;

               for(int var15 = 0; var15 < var6; ++var15) {
                  int var16 = var4 + var15;
                  if (var15 >= var23 && var24 > 0) {
                     var25 += Direction.field_71583_a[var22];
                     var13 += Direction.field_71581_b[var22];
                     --var24;
                  }

                  Block var17 = var1.func_147439_a(var25, var16, var13);
                  if (var17.func_149688_o() == Material.field_151579_a || var17.func_149688_o() == Material.field_151584_j) {
                     this.func_150516_a(var1, var25, var16, var13, Blocks.field_150363_s, 0);
                     var14 = var16;
                  }
               }

               for(int var29 = -1; var29 <= 1; ++var29) {
                  for(int var32 = -1; var32 <= 1; ++var32) {
                     this.func_150525_a(var1, var25 + var29, var14 + 1, var13 + var32);
                  }
               }

               this.func_150525_a(var1, var25 + 2, var14 + 1, var13);
               this.func_150525_a(var1, var25 - 2, var14 + 1, var13);
               this.func_150525_a(var1, var25, var14 + 1, var13 + 2);
               this.func_150525_a(var1, var25, var14 + 1, var13 - 2);

               for(int var30 = -3; var30 <= 3; ++var30) {
                  for(int var33 = -3; var33 <= 3; ++var33) {
                     if (Math.abs(var30) != 3 || Math.abs(var33) != 3) {
                        this.func_150525_a(var1, var25 + var30, var14, var13 + var33);
                     }
                  }
               }

               var25 = var3;
               var13 = var5;
               int var31 = var2.nextInt(4);
               if (var31 != var22) {
                  int var34 = var23 - var2.nextInt(2) - 1;
                  int var35 = 1 + var2.nextInt(3);
                  var14 = 0;

                  for(int var18 = var34; var18 < var6 && var35 > 0; --var35) {
                     if (var18 >= 1) {
                        int var19 = var4 + var18;
                        var25 += Direction.field_71583_a[var31];
                        var13 += Direction.field_71581_b[var31];
                        Block var20 = var1.func_147439_a(var25, var19, var13);
                        if (var20.func_149688_o() == Material.field_151579_a || var20.func_149688_o() == Material.field_151584_j) {
                           this.func_150516_a(var1, var25, var19, var13, Blocks.field_150363_s, 0);
                           var14 = var19;
                        }
                     }

                     ++var18;
                  }

                  if (var14 > 0) {
                     for(int var36 = -1; var36 <= 1; ++var36) {
                        for(int var38 = -1; var38 <= 1; ++var38) {
                           this.func_150525_a(var1, var25 + var36, var14 + 1, var13 + var38);
                        }
                     }

                     for(int var37 = -2; var37 <= 2; ++var37) {
                        for(int var39 = -2; var39 <= 2; ++var39) {
                           if (Math.abs(var37) != 2 || Math.abs(var39) != 2) {
                              this.func_150525_a(var1, var25 + var37, var14, var13 + var39);
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

   private void func_150525_a(World var1, int var2, int var3, int var4) {
      Block var5 = var1.func_147439_a(var2, var3, var4);
      if (var5.func_149688_o() == Material.field_151579_a || var5.func_149688_o() == Material.field_151584_j) {
         this.func_150516_a(var1, var2, var3, var4, Blocks.field_150361_u, 0);
      }
   }
}
