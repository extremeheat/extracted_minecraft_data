package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenClay extends WorldGenerator {
   private Block field_150546_a = Blocks.field_150435_aG;
   private int field_76517_b;

   public WorldGenClay(int var1) {
      super();
      this.field_76517_b = var1;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      if (var1.func_147439_a(var3, var4, var5).func_149688_o() != Material.field_151586_h) {
         return false;
      } else {
         int var6 = var2.nextInt(this.field_76517_b - 2) + 2;
         byte var7 = 1;

         for(int var8 = var3 - var6; var8 <= var3 + var6; ++var8) {
            for(int var9 = var5 - var6; var9 <= var5 + var6; ++var9) {
               int var10 = var8 - var3;
               int var11 = var9 - var5;
               if (var10 * var10 + var11 * var11 <= var6 * var6) {
                  for(int var12 = var4 - var7; var12 <= var4 + var7; ++var12) {
                     Block var13 = var1.func_147439_a(var8, var12, var9);
                     if (var13 == Blocks.field_150346_d || var13 == Blocks.field_150435_aG) {
                        var1.func_147465_d(var8, var12, var9, this.field_150546_a, 0, 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
