package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public abstract class WorldGenHugeTrees extends WorldGenAbstractTree {
   protected final int field_76522_a;
   protected final int field_76520_b;
   protected final int field_76521_c;
   protected int field_150538_d;

   public WorldGenHugeTrees(boolean var1, int var2, int var3, int var4, int var5) {
      super(var1);
      this.field_76522_a = var2;
      this.field_150538_d = var3;
      this.field_76520_b = var4;
      this.field_76521_c = var5;
   }

   protected int func_150533_a(Random var1) {
      int var2 = var1.nextInt(3) + this.field_76522_a;
      if (this.field_150538_d > 1) {
         var2 += var1.nextInt(this.field_150538_d);
      }

      return var2;
   }

   private boolean func_150536_b(World var1, Random var2, int var3, int var4, int var5, int var6) {
      boolean var7 = true;
      if (var4 >= 1 && var4 + var6 + 1 <= 256) {
         for(int var8 = var4; var8 <= var4 + 1 + var6; ++var8) {
            byte var9 = 2;
            if (var8 == var4) {
               var9 = 1;
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

         return var7;
      } else {
         return false;
      }
   }

   private boolean func_150532_c(World var1, Random var2, int var3, int var4, int var5) {
      Block var6 = var1.func_147439_a(var3, var4 - 1, var5);
      if ((var6 == Blocks.field_150349_c || var6 == Blocks.field_150346_d) && var4 >= 2) {
         var1.func_147465_d(var3, var4 - 1, var5, Blocks.field_150346_d, 0, 2);
         var1.func_147465_d(var3 + 1, var4 - 1, var5, Blocks.field_150346_d, 0, 2);
         var1.func_147465_d(var3, var4 - 1, var5 + 1, Blocks.field_150346_d, 0, 2);
         var1.func_147465_d(var3 + 1, var4 - 1, var5 + 1, Blocks.field_150346_d, 0, 2);
         return true;
      } else {
         return false;
      }
   }

   protected boolean func_150537_a(World var1, Random var2, int var3, int var4, int var5, int var6) {
      return this.func_150536_b(var1, var2, var3, var4, var5, var6) && this.func_150532_c(var1, var2, var3, var4, var5);
   }

   protected void func_150535_a(World var1, int var2, int var3, int var4, int var5, Random var6) {
      int var7 = var5 * var5;

      for(int var8 = var2 - var5; var8 <= var2 + var5 + 1; ++var8) {
         int var9 = var8 - var2;

         for(int var10 = var4 - var5; var10 <= var4 + var5 + 1; ++var10) {
            int var11 = var10 - var4;
            int var12 = var9 - 1;
            int var13 = var11 - 1;
            if (var9 * var9 + var11 * var11 <= var7
               || var12 * var12 + var13 * var13 <= var7
               || var9 * var9 + var13 * var13 <= var7
               || var12 * var12 + var11 * var11 <= var7) {
               Block var14 = var1.func_147439_a(var8, var3, var10);
               if (var14.func_149688_o() == Material.field_151579_a || var14.func_149688_o() == Material.field_151584_j) {
                  this.func_150516_a(var1, var8, var3, var10, Blocks.field_150362_t, this.field_76521_c);
               }
            }
         }
      }
   }

   protected void func_150534_b(World var1, int var2, int var3, int var4, int var5, Random var6) {
      int var7 = var5 * var5;

      for(int var8 = var2 - var5; var8 <= var2 + var5; ++var8) {
         int var9 = var8 - var2;

         for(int var10 = var4 - var5; var10 <= var4 + var5; ++var10) {
            int var11 = var10 - var4;
            if (var9 * var9 + var11 * var11 <= var7) {
               Block var12 = var1.func_147439_a(var8, var3, var10);
               if (var12.func_149688_o() == Material.field_151579_a || var12.func_149688_o() == Material.field_151584_j) {
                  this.func_150516_a(var1, var8, var3, var10, Blocks.field_150362_t, this.field_76521_c);
               }
            }
         }
      }
   }
}
