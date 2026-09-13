package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenShrub extends WorldGenTrees {
   private int field_150528_a;
   private int field_150527_b;

   public WorldGenShrub(int var1, int var2) {
      super(false);
      this.field_150527_b = var1;
      this.field_150528_a = var2;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      Block var6;
      while(
         ((var6 = var1.func_147439_a(var3, var4, var5)).func_149688_o() == Material.field_151579_a || var6.func_149688_o() == Material.field_151584_j)
            && var4 > 0
      ) {
         --var4;
      }

      Block var7 = var1.func_147439_a(var3, var4, var5);
      if (var7 == Blocks.field_150346_d || var7 == Blocks.field_150349_c) {
         this.func_150516_a(var1, var3, ++var4, var5, Blocks.field_150364_r, this.field_150527_b);

         for(int var8 = var4; var8 <= var4 + 2; ++var8) {
            int var9 = var8 - var4;
            int var10 = 2 - var9;

            for(int var11 = var3 - var10; var11 <= var3 + var10; ++var11) {
               int var12 = var11 - var3;

               for(int var13 = var5 - var10; var13 <= var5 + var10; ++var13) {
                  int var14 = var13 - var5;
                  if ((Math.abs(var12) != var10 || Math.abs(var14) != var10 || var2.nextInt(2) != 0)
                     && !var1.func_147439_a(var11, var8, var13).func_149730_j()) {
                     this.func_150516_a(var1, var11, var8, var13, Blocks.field_150362_t, this.field_150528_a);
                  }
               }
            }
         }
      }

      return true;
   }
}
