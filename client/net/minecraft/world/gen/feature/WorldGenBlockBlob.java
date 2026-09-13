package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenBlockBlob extends WorldGenerator {
   private Block field_150545_a;
   private int field_150544_b;

   public WorldGenBlockBlob(Block var1, int var2) {
      super(false);
      this.field_150545_a = var1;
      this.field_150544_b = var2;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      for(; var4 > 3; --var4) {
         if (!var1.func_147437_c(var3, var4 - 1, var5)) {
            Block var6 = var1.func_147439_a(var3, var4 - 1, var5);
            if (var6 == Blocks.field_150349_c || var6 == Blocks.field_150346_d || var6 == Blocks.field_150348_b) {
               break;
            }
         }
      }

      if (var4 <= 3) {
         return false;
      } else {
         int var18 = this.field_150544_b;

         for(int var7 = 0; var18 >= 0 && var7 < 3; ++var7) {
            int var8 = var18 + var2.nextInt(2);
            int var9 = var18 + var2.nextInt(2);
            int var10 = var18 + var2.nextInt(2);
            float var11 = (float)(var8 + var9 + var10) * 0.333F + 0.5F;

            for(int var12 = var3 - var8; var12 <= var3 + var8; ++var12) {
               for(int var13 = var5 - var10; var13 <= var5 + var10; ++var13) {
                  for(int var14 = var4 - var9; var14 <= var4 + var9; ++var14) {
                     float var15 = (float)(var12 - var3);
                     float var16 = (float)(var13 - var5);
                     float var17 = (float)(var14 - var4);
                     if (!(var15 * var15 + var16 * var16 + var17 * var17 > var11 * var11)) {
                        var1.func_147465_d(var12, var14, var13, this.field_150545_a, 0, 4);
                     }
                  }
               }
            }

            var3 += -(var18 + 1) + var2.nextInt(2 + var18 * 2);
            var5 += -(var18 + 1) + var2.nextInt(2 + var18 * 2);
            var4 += 0 - var2.nextInt(2);
         }

         return true;
      }
   }
}
