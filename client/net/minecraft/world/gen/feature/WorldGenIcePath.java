package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenIcePath extends WorldGenerator {
   private Block field_150555_a = Blocks.field_150403_cj;
   private int field_150554_b;

   public WorldGenIcePath(int var1) {
      super();
      this.field_150554_b = var1;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      while(var1.func_147437_c(var3, var4, var5) && var4 > 2) {
         --var4;
      }

      if (var1.func_147439_a(var3, var4, var5) != Blocks.field_150433_aE) {
         return false;
      } else {
         int var6 = var2.nextInt(this.field_150554_b - 2) + 2;
         byte var7 = 1;

         for(int var8 = var3 - var6; var8 <= var3 + var6; ++var8) {
            for(int var9 = var5 - var6; var9 <= var5 + var6; ++var9) {
               int var10 = var8 - var3;
               int var11 = var9 - var5;
               if (var10 * var10 + var11 * var11 <= var6 * var6) {
                  for(int var12 = var4 - var7; var12 <= var4 + var7; ++var12) {
                     Block var13 = var1.func_147439_a(var8, var12, var9);
                     if (var13 == Blocks.field_150346_d || var13 == Blocks.field_150433_aE || var13 == Blocks.field_150432_aD) {
                        var1.func_147465_d(var8, var12, var9, this.field_150555_a, 0, 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
