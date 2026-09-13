package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenSwamp extends WorldGenAbstractTree {
   public WorldGenSwamp() {
      super(false);
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = var2.nextInt(4) + 5;

      while(var1.func_147439_a(var3, var4 - 1, var5).func_149688_o() == Material.field_151586_h) {
         --var4;
      }

      boolean var7 = true;
      if (var4 >= 1 && var4 + var6 + 1 <= 256) {
         for(int var8 = var4; var8 <= var4 + 1 + var6; ++var8) {
            byte var9 = 1;
            if (var8 == var4) {
               var9 = 0;
            }

            if (var8 >= var4 + 1 + var6 - 2) {
               var9 = 3;
            }

            for(int var10 = var3 - var9; var10 <= var3 + var9 && var7; ++var10) {
               for(int var11 = var5 - var9; var11 <= var5 + var9 && var7; ++var11) {
                  if (var8 >= 0 && var8 < 256) {
                     Block var12 = var1.func_147439_a(var10, var8, var11);
                     if (var12.func_149688_o() != Material.field_151579_a && var12.func_149688_o() != Material.field_151584_j) {
                        if (var12 != Blocks.field_150355_j && var12 != Blocks.field_150358_i) {
                           var7 = false;
                        } else if (var8 > var4) {
                           var7 = false;
                        }
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
            Block var16 = var1.func_147439_a(var3, var4 - 1, var5);
            if ((var16 == Blocks.field_150349_c || var16 == Blocks.field_150346_d) && var4 < 256 - var6 - 1) {
               this.func_150515_a(var1, var3, var4 - 1, var5, Blocks.field_150346_d);

               for(int var17 = var4 - 3 + var6; var17 <= var4 + var6; ++var17) {
                  int var20 = var17 - (var4 + var6);
                  int var23 = 2 - var20 / 2;

                  for(int var25 = var3 - var23; var25 <= var3 + var23; ++var25) {
                     int var13 = var25 - var3;

                     for(int var14 = var5 - var23; var14 <= var5 + var23; ++var14) {
                        int var15 = var14 - var5;
                        if ((Math.abs(var13) != var23 || Math.abs(var15) != var23 || var2.nextInt(2) != 0 && var20 != 0)
                           && !var1.func_147439_a(var25, var17, var14).func_149730_j()) {
                           this.func_150515_a(var1, var25, var17, var14, Blocks.field_150362_t);
                        }
                     }
                  }
               }

               for(int var18 = 0; var18 < var6; ++var18) {
                  Block var21 = var1.func_147439_a(var3, var4 + var18, var5);
                  if (var21.func_149688_o() == Material.field_151579_a
                     || var21.func_149688_o() == Material.field_151584_j
                     || var21 == Blocks.field_150358_i
                     || var21 == Blocks.field_150355_j) {
                     this.func_150515_a(var1, var3, var4 + var18, var5, Blocks.field_150364_r);
                  }
               }

               for(int var19 = var4 - 3 + var6; var19 <= var4 + var6; ++var19) {
                  int var22 = var19 - (var4 + var6);
                  int var24 = 2 - var22 / 2;

                  for(int var26 = var3 - var24; var26 <= var3 + var24; ++var26) {
                     for(int var27 = var5 - var24; var27 <= var5 + var24; ++var27) {
                        if (var1.func_147439_a(var26, var19, var27).func_149688_o() == Material.field_151584_j) {
                           if (var2.nextInt(4) == 0 && var1.func_147439_a(var26 - 1, var19, var27).func_149688_o() == Material.field_151579_a) {
                              this.func_76536_b(var1, var26 - 1, var19, var27, 8);
                           }

                           if (var2.nextInt(4) == 0 && var1.func_147439_a(var26 + 1, var19, var27).func_149688_o() == Material.field_151579_a) {
                              this.func_76536_b(var1, var26 + 1, var19, var27, 2);
                           }

                           if (var2.nextInt(4) == 0 && var1.func_147439_a(var26, var19, var27 - 1).func_149688_o() == Material.field_151579_a) {
                              this.func_76536_b(var1, var26, var19, var27 - 1, 1);
                           }

                           if (var2.nextInt(4) == 0 && var1.func_147439_a(var26, var19, var27 + 1).func_149688_o() == Material.field_151579_a) {
                              this.func_76536_b(var1, var26, var19, var27 + 1, 4);
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

   private void func_76536_b(World var1, int var2, int var3, int var4, int var5) {
      this.func_150516_a(var1, var2, var3, var4, Blocks.field_150395_bd, var5);

      for(int var6 = 4; var1.func_147439_a(var2, --var3, var4).func_149688_o() == Material.field_151579_a && var6 > 0; --var6) {
         this.func_150516_a(var1, var2, var3, var4, Blocks.field_150395_bd, var5);
      }
   }
}
