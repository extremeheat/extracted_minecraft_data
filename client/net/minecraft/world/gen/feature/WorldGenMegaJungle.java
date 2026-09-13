package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees {
   public WorldGenMegaJungle(boolean var1, int var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = this.func_150533_a(var2);
      if (!this.func_150537_a(var1, var2, var3, var4, var5, var6)) {
         return false;
      } else {
         this.func_150543_c(var1, var3, var5, var4 + var6, 2, var2);

         for(int var7 = var4 + var6 - 2 - var2.nextInt(4); var7 > var4 + var6 / 2; var7 -= 2 + var2.nextInt(4)) {
            float var8 = var2.nextFloat() * 3.1415927F * 2.0F;
            int var9 = var3 + (int)(0.5F + MathHelper.func_76134_b(var8) * 4.0F);
            int var10 = var5 + (int)(0.5F + MathHelper.func_76126_a(var8) * 4.0F);

            for(int var11 = 0; var11 < 5; ++var11) {
               var9 = var3 + (int)(1.5F + MathHelper.func_76134_b(var8) * (float)var11);
               var10 = var5 + (int)(1.5F + MathHelper.func_76126_a(var8) * (float)var11);
               this.func_150516_a(var1, var9, var7 - 3 + var11 / 2, var10, Blocks.field_150364_r, this.field_76520_b);
            }

            int var20 = 1 + var2.nextInt(2);
            int var12 = var7;

            for(int var13 = var7 - var20; var13 <= var12; ++var13) {
               int var14 = var13 - var12;
               this.func_150534_b(var1, var9, var13, var10, 1 - var14, var2);
            }
         }

         for(int var15 = 0; var15 < var6; ++var15) {
            Block var16 = var1.func_147439_a(var3, var4 + var15, var5);
            if (var16.func_149688_o() == Material.field_151579_a || var16.func_149688_o() == Material.field_151584_j) {
               this.func_150516_a(var1, var3, var4 + var15, var5, Blocks.field_150364_r, this.field_76520_b);
               if (var15 > 0) {
                  if (var2.nextInt(3) > 0 && var1.func_147437_c(var3 - 1, var4 + var15, var5)) {
                     this.func_150516_a(var1, var3 - 1, var4 + var15, var5, Blocks.field_150395_bd, 8);
                  }

                  if (var2.nextInt(3) > 0 && var1.func_147437_c(var3, var4 + var15, var5 - 1)) {
                     this.func_150516_a(var1, var3, var4 + var15, var5 - 1, Blocks.field_150395_bd, 1);
                  }
               }
            }

            if (var15 < var6 - 1) {
               var16 = var1.func_147439_a(var3 + 1, var4 + var15, var5);
               if (var16.func_149688_o() == Material.field_151579_a || var16.func_149688_o() == Material.field_151584_j) {
                  this.func_150516_a(var1, var3 + 1, var4 + var15, var5, Blocks.field_150364_r, this.field_76520_b);
                  if (var15 > 0) {
                     if (var2.nextInt(3) > 0 && var1.func_147437_c(var3 + 2, var4 + var15, var5)) {
                        this.func_150516_a(var1, var3 + 2, var4 + var15, var5, Blocks.field_150395_bd, 2);
                     }

                     if (var2.nextInt(3) > 0 && var1.func_147437_c(var3 + 1, var4 + var15, var5 - 1)) {
                        this.func_150516_a(var1, var3 + 1, var4 + var15, var5 - 1, Blocks.field_150395_bd, 1);
                     }
                  }
               }

               var16 = var1.func_147439_a(var3 + 1, var4 + var15, var5 + 1);
               if (var16.func_149688_o() == Material.field_151579_a || var16.func_149688_o() == Material.field_151584_j) {
                  this.func_150516_a(var1, var3 + 1, var4 + var15, var5 + 1, Blocks.field_150364_r, this.field_76520_b);
                  if (var15 > 0) {
                     if (var2.nextInt(3) > 0 && var1.func_147437_c(var3 + 2, var4 + var15, var5 + 1)) {
                        this.func_150516_a(var1, var3 + 2, var4 + var15, var5 + 1, Blocks.field_150395_bd, 2);
                     }

                     if (var2.nextInt(3) > 0 && var1.func_147437_c(var3 + 1, var4 + var15, var5 + 2)) {
                        this.func_150516_a(var1, var3 + 1, var4 + var15, var5 + 2, Blocks.field_150395_bd, 4);
                     }
                  }
               }

               var16 = var1.func_147439_a(var3, var4 + var15, var5 + 1);
               if (var16.func_149688_o() == Material.field_151579_a || var16.func_149688_o() == Material.field_151584_j) {
                  this.func_150516_a(var1, var3, var4 + var15, var5 + 1, Blocks.field_150364_r, this.field_76520_b);
                  if (var15 > 0) {
                     if (var2.nextInt(3) > 0 && var1.func_147437_c(var3 - 1, var4 + var15, var5 + 1)) {
                        this.func_150516_a(var1, var3 - 1, var4 + var15, var5 + 1, Blocks.field_150395_bd, 8);
                     }

                     if (var2.nextInt(3) > 0 && var1.func_147437_c(var3, var4 + var15, var5 + 2)) {
                        this.func_150516_a(var1, var3, var4 + var15, var5 + 2, Blocks.field_150395_bd, 4);
                     }
                  }
               }
            }
         }

         return true;
      }
   }

   private void func_150543_c(World var1, int var2, int var3, int var4, int var5, Random var6) {
      byte var7 = 2;

      for(int var8 = var4 - var7; var8 <= var4; ++var8) {
         int var9 = var8 - var4;
         this.func_150535_a(var1, var2, var8, var3, var5 + 1 - var9, var6);
      }
   }
}
