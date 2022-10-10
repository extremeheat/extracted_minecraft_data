package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class TallGrassFeature extends Feature<TallGrassConfig> {
   public TallGrassFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, TallGrassConfig var5) {
      for(IBlockState var6 = var1.func_180495_p(var4); (var6.func_196958_f() || var6.func_203425_a(BlockTags.field_206952_E)) && var4.func_177956_o() > 0; var6 = var1.func_180495_p(var4)) {
         var4 = var4.func_177977_b();
      }

      int var7 = 0;

      for(int var8 = 0; var8 < 128; ++var8) {
         BlockPos var9 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.func_175623_d(var9) && var5.field_202460_a.func_196955_c(var1, var9)) {
            var1.func_180501_a(var9, var5.field_202460_a, 2);
            ++var7;
         }
      }

      return var7 > 0;
   }
}
