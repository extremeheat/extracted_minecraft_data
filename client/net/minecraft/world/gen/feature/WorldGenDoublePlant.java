package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator {
   private BlockDoublePlant.EnumPlantType field_150549_a;

   public WorldGenDoublePlant() {
      super();
   }

   public void func_180710_a(BlockDoublePlant.EnumPlantType var1) {
      this.field_150549_a = var1;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      boolean var4 = false;

      for(int var5 = 0; var5 < 64; ++var5) {
         BlockPos var6 = var3.func_177982_a(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.func_175623_d(var6) && (!var1.field_73011_w.func_177495_o() || var6.func_177956_o() < 254) && Blocks.field_150398_cm.func_176196_c(var1, var6)) {
            Blocks.field_150398_cm.func_176491_a(var1, var6, this.field_150549_a, 2);
            var4 = true;
         }
      }

      return var4;
   }
}
