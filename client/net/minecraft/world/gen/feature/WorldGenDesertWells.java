package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenDesertWells extends WorldGenerator {
   public WorldGenDesertWells() {
      super();
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      while(var1.func_147437_c(var3, var4, var5) && var4 > 2) {
         --var4;
      }

      if (var1.func_147439_a(var3, var4, var5) != Blocks.field_150354_m) {
         return false;
      } else {
         for(int var6 = -2; var6 <= 2; ++var6) {
            for(int var7 = -2; var7 <= 2; ++var7) {
               if (var1.func_147437_c(var3 + var6, var4 - 1, var5 + var7) && var1.func_147437_c(var3 + var6, var4 - 2, var5 + var7)) {
                  return false;
               }
            }
         }

         for(int var9 = -1; var9 <= 0; ++var9) {
            for(int var13 = -2; var13 <= 2; ++var13) {
               for(int var8 = -2; var8 <= 2; ++var8) {
                  var1.func_147465_d(var3 + var13, var4 + var9, var5 + var8, Blocks.field_150322_A, 0, 2);
               }
            }
         }

         var1.func_147465_d(var3, var4, var5, Blocks.field_150358_i, 0, 2);
         var1.func_147465_d(var3 - 1, var4, var5, Blocks.field_150358_i, 0, 2);
         var1.func_147465_d(var3 + 1, var4, var5, Blocks.field_150358_i, 0, 2);
         var1.func_147465_d(var3, var4, var5 - 1, Blocks.field_150358_i, 0, 2);
         var1.func_147465_d(var3, var4, var5 + 1, Blocks.field_150358_i, 0, 2);

         for(int var10 = -2; var10 <= 2; ++var10) {
            for(int var14 = -2; var14 <= 2; ++var14) {
               if (var10 == -2 || var10 == 2 || var14 == -2 || var14 == 2) {
                  var1.func_147465_d(var3 + var10, var4 + 1, var5 + var14, Blocks.field_150322_A, 0, 2);
               }
            }
         }

         var1.func_147465_d(var3 + 2, var4 + 1, var5, Blocks.field_150333_U, 1, 2);
         var1.func_147465_d(var3 - 2, var4 + 1, var5, Blocks.field_150333_U, 1, 2);
         var1.func_147465_d(var3, var4 + 1, var5 + 2, Blocks.field_150333_U, 1, 2);
         var1.func_147465_d(var3, var4 + 1, var5 - 2, Blocks.field_150333_U, 1, 2);

         for(int var11 = -1; var11 <= 1; ++var11) {
            for(int var15 = -1; var15 <= 1; ++var15) {
               if (var11 == 0 && var15 == 0) {
                  var1.func_147465_d(var3 + var11, var4 + 4, var5 + var15, Blocks.field_150322_A, 0, 2);
               } else {
                  var1.func_147465_d(var3 + var11, var4 + 4, var5 + var15, Blocks.field_150333_U, 1, 2);
               }
            }
         }

         for(int var12 = 1; var12 <= 3; ++var12) {
            var1.func_147465_d(var3 - 1, var4 + var12, var5 - 1, Blocks.field_150322_A, 0, 2);
            var1.func_147465_d(var3 - 1, var4 + var12, var5 + 1, Blocks.field_150322_A, 0, 2);
            var1.func_147465_d(var3 + 1, var4 + var12, var5 - 1, Blocks.field_150322_A, 0, 2);
            var1.func_147465_d(var3 + 1, var4 + var12, var5 + 1, Blocks.field_150322_A, 0, 2);
         }

         return true;
      }
   }
}
