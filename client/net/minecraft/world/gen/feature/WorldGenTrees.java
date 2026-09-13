package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class WorldGenTrees extends WorldGenAbstractTree {
   private final int field_76533_a;
   private final boolean field_76531_b;
   private final int field_76532_c;
   private final int field_76530_d;

   public WorldGenTrees(boolean var1) {
      this(var1, 4, 0, 0, false);
   }

   public WorldGenTrees(boolean var1, int var2, int var3, int var4, boolean var5) {
      super(var1);
      this.field_76533_a = var2;
      this.field_76532_c = var3;
      this.field_76530_d = var4;
      this.field_76531_b = var5;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = var2.nextInt(3) + this.field_76533_a;
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
            Block var19 = var1.func_147439_a(var3, var4 - 1, var5);
            if ((var19 == Blocks.field_150349_c || var19 == Blocks.field_150346_d || var19 == Blocks.field_150458_ak) && var4 < 256 - var6 - 1) {
               this.func_150515_a(var1, var3, var4 - 1, var5, Blocks.field_150346_d);
               byte var20 = 3;
               byte var21 = 0;

               for(int var22 = var4 - var20 + var6; var22 <= var4 + var6; ++var22) {
                  int var26 = var22 - (var4 + var6);
                  int var13 = var21 + 1 - var26 / 2;

                  for(int var14 = var3 - var13; var14 <= var3 + var13; ++var14) {
                     int var15 = var14 - var3;

                     for(int var16 = var5 - var13; var16 <= var5 + var13; ++var16) {
                        int var17 = var16 - var5;
                        if (Math.abs(var15) != var13 || Math.abs(var17) != var13 || var2.nextInt(2) != 0 && var26 != 0) {
                           Block var18 = var1.func_147439_a(var14, var22, var16);
                           if (var18.func_149688_o() == Material.field_151579_a || var18.func_149688_o() == Material.field_151584_j) {
                              this.func_150516_a(var1, var14, var22, var16, Blocks.field_150362_t, this.field_76530_d);
                           }
                        }
                     }
                  }
               }

               for(int var23 = 0; var23 < var6; ++var23) {
                  Block var27 = var1.func_147439_a(var3, var4 + var23, var5);
                  if (var27.func_149688_o() == Material.field_151579_a || var27.func_149688_o() == Material.field_151584_j) {
                     this.func_150516_a(var1, var3, var4 + var23, var5, Blocks.field_150364_r, this.field_76532_c);
                     if (this.field_76531_b && var23 > 0) {
                        if (var2.nextInt(3) > 0 && var1.func_147437_c(var3 - 1, var4 + var23, var5)) {
                           this.func_150516_a(var1, var3 - 1, var4 + var23, var5, Blocks.field_150395_bd, 8);
                        }

                        if (var2.nextInt(3) > 0 && var1.func_147437_c(var3 + 1, var4 + var23, var5)) {
                           this.func_150516_a(var1, var3 + 1, var4 + var23, var5, Blocks.field_150395_bd, 2);
                        }

                        if (var2.nextInt(3) > 0 && var1.func_147437_c(var3, var4 + var23, var5 - 1)) {
                           this.func_150516_a(var1, var3, var4 + var23, var5 - 1, Blocks.field_150395_bd, 1);
                        }

                        if (var2.nextInt(3) > 0 && var1.func_147437_c(var3, var4 + var23, var5 + 1)) {
                           this.func_150516_a(var1, var3, var4 + var23, var5 + 1, Blocks.field_150395_bd, 4);
                        }
                     }
                  }
               }

               if (this.field_76531_b) {
                  for(int var24 = var4 - 3 + var6; var24 <= var4 + var6; ++var24) {
                     int var28 = var24 - (var4 + var6);
                     int var30 = 2 - var28 / 2;

                     for(int var32 = var3 - var30; var32 <= var3 + var30; ++var32) {
                        for(int var33 = var5 - var30; var33 <= var5 + var30; ++var33) {
                           if (var1.func_147439_a(var32, var24, var33).func_149688_o() == Material.field_151584_j) {
                              if (var2.nextInt(4) == 0 && var1.func_147439_a(var32 - 1, var24, var33).func_149688_o() == Material.field_151579_a) {
                                 this.func_76529_b(var1, var32 - 1, var24, var33, 8);
                              }

                              if (var2.nextInt(4) == 0 && var1.func_147439_a(var32 + 1, var24, var33).func_149688_o() == Material.field_151579_a) {
                                 this.func_76529_b(var1, var32 + 1, var24, var33, 2);
                              }

                              if (var2.nextInt(4) == 0 && var1.func_147439_a(var32, var24, var33 - 1).func_149688_o() == Material.field_151579_a) {
                                 this.func_76529_b(var1, var32, var24, var33 - 1, 1);
                              }

                              if (var2.nextInt(4) == 0 && var1.func_147439_a(var32, var24, var33 + 1).func_149688_o() == Material.field_151579_a) {
                                 this.func_76529_b(var1, var32, var24, var33 + 1, 4);
                              }
                           }
                        }
                     }
                  }

                  if (var2.nextInt(5) == 0 && var6 > 5) {
                     for(int var25 = 0; var25 < 2; ++var25) {
                        for(int var29 = 0; var29 < 4; ++var29) {
                           if (var2.nextInt(4 - var25) == 0) {
                              int var31 = var2.nextInt(3);
                              this.func_150516_a(
                                 var1,
                                 var3 + Direction.field_71583_a[Direction.field_71580_e[var29]],
                                 var4 + var6 - 5 + var25,
                                 var5 + Direction.field_71581_b[Direction.field_71580_e[var29]],
                                 Blocks.field_150375_by,
                                 var31 << 2 | var29
                              );
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

   private void func_76529_b(World var1, int var2, int var3, int var4, int var5) {
      this.func_150516_a(var1, var2, var3, var4, Blocks.field_150395_bd, var5);

      for(int var6 = 4; var1.func_147439_a(var2, --var3, var4).func_149688_o() == Material.field_151579_a && var6 > 0; --var6) {
         this.func_150516_a(var1, var2, var3, var4, Blocks.field_150395_bd, var5);
      }
   }
}
