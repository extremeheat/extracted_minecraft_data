package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaPineTree extends WorldGenHugeTrees {
   private boolean field_150542_e;

   public WorldGenMegaPineTree(boolean var1, boolean var2) {
      super(var1, 13, 15, 1, 1);
      this.field_150542_e = var2;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = this.func_150533_a(var2);
      if (!this.func_150537_a(var1, var2, var3, var4, var5, var6)) {
         return false;
      } else {
         this.func_150541_c(var1, var3, var5, var4 + var6, 0, var2);

         for(int var7 = 0; var7 < var6; ++var7) {
            Block var8 = var1.func_147439_a(var3, var4 + var7, var5);
            if (var8.func_149688_o() == Material.field_151579_a || var8.func_149688_o() == Material.field_151584_j) {
               this.func_150516_a(var1, var3, var4 + var7, var5, Blocks.field_150364_r, this.field_76520_b);
            }

            if (var7 < var6 - 1) {
               var8 = var1.func_147439_a(var3 + 1, var4 + var7, var5);
               if (var8.func_149688_o() == Material.field_151579_a || var8.func_149688_o() == Material.field_151584_j) {
                  this.func_150516_a(var1, var3 + 1, var4 + var7, var5, Blocks.field_150364_r, this.field_76520_b);
               }

               var8 = var1.func_147439_a(var3 + 1, var4 + var7, var5 + 1);
               if (var8.func_149688_o() == Material.field_151579_a || var8.func_149688_o() == Material.field_151584_j) {
                  this.func_150516_a(var1, var3 + 1, var4 + var7, var5 + 1, Blocks.field_150364_r, this.field_76520_b);
               }

               var8 = var1.func_147439_a(var3, var4 + var7, var5 + 1);
               if (var8.func_149688_o() == Material.field_151579_a || var8.func_149688_o() == Material.field_151584_j) {
                  this.func_150516_a(var1, var3, var4 + var7, var5 + 1, Blocks.field_150364_r, this.field_76520_b);
               }
            }
         }

         return true;
      }
   }

   private void func_150541_c(World var1, int var2, int var3, int var4, int var5, Random var6) {
      int var7 = var6.nextInt(5);
      if (this.field_150542_e) {
         var7 += this.field_76522_a;
      } else {
         var7 += 3;
      }

      int var8 = 0;

      for(int var9 = var4 - var7; var9 <= var4; ++var9) {
         int var10 = var4 - var9;
         int var11 = var5 + MathHelper.func_76141_d((float)var10 / (float)var7 * 3.5F);
         this.func_150535_a(var1, var2, var9, var3, var11 + (var10 > 0 && var11 == var8 && (var9 & 1) == 0 ? 1 : 0), var6);
         var8 = var11;
      }
   }

   @Override
   public void func_150524_b(World var1, Random var2, int var3, int var4, int var5) {
      this.func_150539_c(var1, var2, var3 - 1, var4, var5 - 1);
      this.func_150539_c(var1, var2, var3 + 2, var4, var5 - 1);
      this.func_150539_c(var1, var2, var3 - 1, var4, var5 + 2);
      this.func_150539_c(var1, var2, var3 + 2, var4, var5 + 2);

      for(int var6 = 0; var6 < 5; ++var6) {
         int var7 = var2.nextInt(64);
         int var8 = var7 % 8;
         int var9 = var7 / 8;
         if (var8 == 0 || var8 == 7 || var9 == 0 || var9 == 7) {
            this.func_150539_c(var1, var2, var3 - 3 + var8, var4, var5 - 3 + var9);
         }
      }
   }

   private void func_150539_c(World var1, Random var2, int var3, int var4, int var5) {
      for(int var6 = -2; var6 <= 2; ++var6) {
         for(int var7 = -2; var7 <= 2; ++var7) {
            if (Math.abs(var6) != 2 || Math.abs(var7) != 2) {
               this.func_150540_a(var1, var3 + var6, var4, var5 + var7);
            }
         }
      }
   }

   private void func_150540_a(World var1, int var2, int var3, int var4) {
      for(int var5 = var3 + 2; var5 >= var3 - 3; --var5) {
         Block var6 = var1.func_147439_a(var2, var5, var4);
         if (var6 == Blocks.field_150349_c || var6 == Blocks.field_150346_d) {
            this.func_150516_a(var1, var2, var5, var4, Blocks.field_150346_d, 2);
            break;
         }

         if (var6.func_149688_o() != Material.field_151579_a && var5 < var3) {
            break;
         }
      }
   }
}
