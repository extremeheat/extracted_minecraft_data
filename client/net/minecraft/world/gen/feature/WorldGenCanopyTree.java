package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class WorldGenCanopyTree extends WorldGenAbstractTree {
   public WorldGenCanopyTree(boolean var1) {
      super(var1);
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = var2.nextInt(3) + var2.nextInt(2) + 6;
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
            Block var20 = var1.func_147439_a(var3, var4 - 1, var5);
            if ((var20 == Blocks.field_150349_c || var20 == Blocks.field_150346_d) && var4 < 256 - var6 - 1) {
               this.func_150515_a(var1, var3, var4 - 1, var5, Blocks.field_150346_d);
               this.func_150515_a(var1, var3 + 1, var4 - 1, var5, Blocks.field_150346_d);
               this.func_150515_a(var1, var3 + 1, var4 - 1, var5 + 1, Blocks.field_150346_d);
               this.func_150515_a(var1, var3, var4 - 1, var5 + 1, Blocks.field_150346_d);
               int var21 = var2.nextInt(4);
               int var22 = var6 - var2.nextInt(4);
               int var23 = 2 - var2.nextInt(3);
               int var24 = var3;
               int var13 = var5;
               int var14 = 0;

               for(int var15 = 0; var15 < var6; ++var15) {
                  int var16 = var4 + var15;
                  if (var15 >= var22 && var23 > 0) {
                     var24 += Direction.field_71583_a[var21];
                     var13 += Direction.field_71581_b[var21];
                     --var23;
                  }

                  Block var17 = var1.func_147439_a(var24, var16, var13);
                  if (var17.func_149688_o() == Material.field_151579_a || var17.func_149688_o() == Material.field_151584_j) {
                     this.func_150516_a(var1, var24, var16, var13, Blocks.field_150363_s, 1);
                     this.func_150516_a(var1, var24 + 1, var16, var13, Blocks.field_150363_s, 1);
                     this.func_150516_a(var1, var24, var16, var13 + 1, Blocks.field_150363_s, 1);
                     this.func_150516_a(var1, var24 + 1, var16, var13 + 1, Blocks.field_150363_s, 1);
                     var14 = var16;
                  }
               }

               for(int var25 = -2; var25 <= 0; ++var25) {
                  for(int var28 = -2; var28 <= 0; ++var28) {
                     byte var31 = -1;
                     this.func_150526_a(var1, var24 + var25, var14 + var31, var13 + var28);
                     this.func_150526_a(var1, 1 + var24 - var25, var14 + var31, var13 + var28);
                     this.func_150526_a(var1, var24 + var25, var14 + var31, 1 + var13 - var28);
                     this.func_150526_a(var1, 1 + var24 - var25, var14 + var31, 1 + var13 - var28);
                     if ((var25 > -2 || var28 > -1) && (var25 != -1 || var28 != -2)) {
                        var31 = 1;
                        this.func_150526_a(var1, var24 + var25, var14 + var31, var13 + var28);
                        this.func_150526_a(var1, 1 + var24 - var25, var14 + var31, var13 + var28);
                        this.func_150526_a(var1, var24 + var25, var14 + var31, 1 + var13 - var28);
                        this.func_150526_a(var1, 1 + var24 - var25, var14 + var31, 1 + var13 - var28);
                     }
                  }
               }

               if (var2.nextBoolean()) {
                  this.func_150526_a(var1, var24, var14 + 2, var13);
                  this.func_150526_a(var1, var24 + 1, var14 + 2, var13);
                  this.func_150526_a(var1, var24 + 1, var14 + 2, var13 + 1);
                  this.func_150526_a(var1, var24, var14 + 2, var13 + 1);
               }

               for(int var26 = -3; var26 <= 4; ++var26) {
                  for(int var29 = -3; var29 <= 4; ++var29) {
                     if ((var26 != -3 || var29 != -3)
                        && (var26 != -3 || var29 != 4)
                        && (var26 != 4 || var29 != -3)
                        && (var26 != 4 || var29 != 4)
                        && (Math.abs(var26) < 3 || Math.abs(var29) < 3)) {
                        this.func_150526_a(var1, var24 + var26, var14, var13 + var29);
                     }
                  }
               }

               for(int var27 = -1; var27 <= 2; ++var27) {
                  for(int var30 = -1; var30 <= 2; ++var30) {
                     if ((var27 < 0 || var27 > 1 || var30 < 0 || var30 > 1) && var2.nextInt(3) <= 0) {
                        int var33 = var2.nextInt(3) + 2;

                        for(int var18 = 0; var18 < var33; ++var18) {
                           this.func_150516_a(var1, var3 + var27, var14 - var18 - 1, var5 + var30, Blocks.field_150363_s, 1);
                        }

                        for(int var34 = -1; var34 <= 1; ++var34) {
                           for(int var19 = -1; var19 <= 1; ++var19) {
                              this.func_150526_a(var1, var24 + var27 + var34, var14 - 0, var13 + var30 + var19);
                           }
                        }

                        for(int var35 = -2; var35 <= 2; ++var35) {
                           for(int var36 = -2; var36 <= 2; ++var36) {
                              if (Math.abs(var35) != 2 || Math.abs(var36) != 2) {
                                 this.func_150526_a(var1, var24 + var27 + var35, var14 - 1, var13 + var30 + var36);
                              }
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

   private void func_150526_a(World var1, int var2, int var3, int var4) {
      Block var5 = var1.func_147439_a(var2, var3, var4);
      if (var5.func_149688_o() == Material.field_151579_a) {
         this.func_150516_a(var1, var2, var3, var4, Blocks.field_150361_u, 1);
      }
   }
}
