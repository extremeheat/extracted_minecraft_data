package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class WorldGenVines extends WorldGenerator {
   public WorldGenVines() {
      super();
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = var3;

      for(int var7 = var5; var4 < 128; ++var4) {
         if (var1.func_147437_c(var3, var4, var5)) {
            for(int var8 = 2; var8 <= 5; ++var8) {
               if (Blocks.field_150395_bd.func_149707_d(var1, var3, var4, var5, var8)) {
                  var1.func_147465_d(var3, var4, var5, Blocks.field_150395_bd, 1 << Direction.field_71579_d[Facing.field_71588_a[var8]], 2);
                  break;
               }
            }
         } else {
            var3 = var6 + var2.nextInt(4) - var2.nextInt(4);
            var5 = var7 + var2.nextInt(4) - var2.nextInt(4);
         }
      }

      return true;
   }
}
