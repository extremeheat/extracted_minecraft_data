package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenCactus extends WorldGenerator {
   public WorldGenCactus() {
      super();
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      for(int var4 = 0; var4 < 10; ++var4) {
         BlockPos var5 = var3.func_177982_a(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.func_175623_d(var5)) {
            int var6 = 1 + var2.nextInt(var2.nextInt(3) + 1);

            for(int var7 = 0; var7 < var6; ++var7) {
               if (Blocks.field_150434_aF.func_176586_d(var1, var5)) {
                  var1.func_180501_a(var5.func_177981_b(var7), Blocks.field_150434_aF.func_176223_P(), 2);
               }
            }
         }
      }

      return true;
   }
}
