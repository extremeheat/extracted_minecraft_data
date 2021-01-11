package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class GeneratorBushFeature extends WorldGenerator {
   private BlockBush field_175908_a;

   public GeneratorBushFeature(BlockBush var1) {
      super();
      this.field_175908_a = var1;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      for(int var4 = 0; var4 < 64; ++var4) {
         BlockPos var5 = var3.func_177982_a(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.func_175623_d(var5) && (!var1.field_73011_w.func_177495_o() || var5.func_177956_o() < 255) && this.field_175908_a.func_180671_f(var1, var5, this.field_175908_a.func_176223_P())) {
            var1.func_180501_a(var5, this.field_175908_a.func_176223_P(), 2);
         }
      }

      return true;
   }
}
