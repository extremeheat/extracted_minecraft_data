package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenWaterlily extends WorldGenerator {
   public WorldGenWaterlily() {
      super();
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      for(int var4 = 0; var4 < 10; ++var4) {
         int var5 = var3.func_177958_n() + var2.nextInt(8) - var2.nextInt(8);
         int var6 = var3.func_177956_o() + var2.nextInt(4) - var2.nextInt(4);
         int var7 = var3.func_177952_p() + var2.nextInt(8) - var2.nextInt(8);
         if (var1.func_175623_d(new BlockPos(var5, var6, var7)) && Blocks.field_150392_bi.func_176196_c(var1, new BlockPos(var5, var6, var7))) {
            var1.func_180501_a(new BlockPos(var5, var6, var7), Blocks.field_150392_bi.func_176223_P(), 2);
         }
      }

      return true;
   }
}
