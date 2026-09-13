package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenReed extends WorldGenerator {
   public WorldGenReed() {
      super();
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      for(int var6 = 0; var6 < 20; ++var6) {
         int var7 = var3 + var2.nextInt(4) - var2.nextInt(4);
         int var8 = var4;
         int var9 = var5 + var2.nextInt(4) - var2.nextInt(4);
         if (var1.func_147437_c(var7, var4, var9)
            && (
               var1.func_147439_a(var7 - 1, var4 - 1, var9).func_149688_o() == Material.field_151586_h
                  || var1.func_147439_a(var7 + 1, var4 - 1, var9).func_149688_o() == Material.field_151586_h
                  || var1.func_147439_a(var7, var4 - 1, var9 - 1).func_149688_o() == Material.field_151586_h
                  || var1.func_147439_a(var7, var4 - 1, var9 + 1).func_149688_o() == Material.field_151586_h
            )) {
            int var10 = 2 + var2.nextInt(var2.nextInt(3) + 1);

            for(int var11 = 0; var11 < var10; ++var11) {
               if (Blocks.field_150436_aH.func_149718_j(var1, var7, var8 + var11, var9)) {
                  var1.func_147465_d(var7, var8 + var11, var9, Blocks.field_150436_aH, 0, 2);
               }
            }
         }
      }

      return true;
   }
}
