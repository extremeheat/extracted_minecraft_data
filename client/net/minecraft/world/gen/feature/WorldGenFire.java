package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenFire extends WorldGenerator {
   public WorldGenFire() {
      super();
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      for(int var4 = 0; var4 < 64; ++var4) {
         BlockPos var5 = var3.func_177982_a(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.func_175623_d(var5) && var1.func_180495_p(var5.func_177977_b()).func_177230_c() == Blocks.field_150424_aL) {
            var1.func_180501_a(var5, Blocks.field_150480_ab.func_176223_P(), 2);
         }
      }

      return true;
   }
}
