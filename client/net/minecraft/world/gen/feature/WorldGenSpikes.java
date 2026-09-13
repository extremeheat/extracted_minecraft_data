package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator {
   private Block field_150520_a;

   public WorldGenSpikes(Block var1) {
      super();
      this.field_150520_a = var1;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      if (var1.func_147437_c(var3, var4, var5) && var1.func_147439_a(var3, var4 - 1, var5) == this.field_150520_a) {
         int var6 = var2.nextInt(32) + 6;
         int var7 = var2.nextInt(4) + 1;

         for(int var8 = var3 - var7; var8 <= var3 + var7; ++var8) {
            for(int var9 = var5 - var7; var9 <= var5 + var7; ++var9) {
               int var10 = var8 - var3;
               int var11 = var9 - var5;
               if (var10 * var10 + var11 * var11 <= var7 * var7 + 1 && var1.func_147439_a(var8, var4 - 1, var9) != this.field_150520_a) {
                  return false;
               }
            }
         }

         for(int var13 = var4; var13 < var4 + var6 && var13 < 256; ++var13) {
            for(int var15 = var3 - var7; var15 <= var3 + var7; ++var15) {
               for(int var16 = var5 - var7; var16 <= var5 + var7; ++var16) {
                  int var17 = var15 - var3;
                  int var12 = var16 - var5;
                  if (var17 * var17 + var12 * var12 <= var7 * var7 + 1) {
                     var1.func_147465_d(var15, var13, var16, Blocks.field_150343_Z, 0, 2);
                  }
               }
            }
         }

         EntityEnderCrystal var14 = new EntityEnderCrystal(var1);
         var14.func_70012_b((double)((float)var3 + 0.5F), (double)(var4 + var6), (double)((float)var5 + 0.5F), var2.nextFloat() * 360.0F, 0.0F);
         var1.func_72838_d(var14);
         var1.func_147465_d(var3, var4 + var6, var5, Blocks.field_150357_h, 0, 2);
         return true;
      } else {
         return false;
      }
   }
}
