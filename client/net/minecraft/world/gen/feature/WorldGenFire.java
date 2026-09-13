package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenFire extends WorldGenerator {
   public WorldGenFire() {
      super();
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      for(int var6 = 0; var6 < 64; ++var6) {
         int var7 = var3 + var2.nextInt(8) - var2.nextInt(8);
         int var8 = var4 + var2.nextInt(4) - var2.nextInt(4);
         int var9 = var5 + var2.nextInt(8) - var2.nextInt(8);
         if (var1.func_147437_c(var7, var8, var9) && var1.func_147439_a(var7, var8 - 1, var9) == Blocks.field_150424_aL) {
            var1.func_147465_d(var7, var8, var9, Blocks.field_150480_ab, 0, 2);
         }
      }

      return true;
   }
}
