package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenPumpkin extends WorldGenerator {
   public WorldGenPumpkin() {
      super();
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      for(int var4 = 0; var4 < 64; ++var4) {
         BlockPos var5 = var3.func_177982_a(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.func_175623_d(var5) && var1.func_180495_p(var5.func_177977_b()).func_177230_c() == Blocks.field_150349_c && Blocks.field_150423_aK.func_176196_c(var1, var5)) {
            var1.func_180501_a(var5, Blocks.field_150423_aK.func_176223_P().func_177226_a(BlockPumpkin.field_176387_N, EnumFacing.Plane.HORIZONTAL.func_179518_a(var2)), 2);
         }
      }

      return true;
   }
}
