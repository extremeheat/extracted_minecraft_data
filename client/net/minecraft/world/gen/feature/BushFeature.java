package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class BushFeature extends Feature<BushConfig> {
   public BushFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, BushConfig var5) {
      int var6 = 0;
      IBlockState var7 = var5.field_202430_a.func_176223_P();

      for(int var8 = 0; var8 < 64; ++var8) {
         BlockPos var9 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.func_175623_d(var9) && (!var1.func_201675_m().func_177495_o() || var9.func_177956_o() < 255) && var7.func_196955_c(var1, var9)) {
            var1.func_180501_a(var9, var7, 2);
            ++var6;
         }
      }

      return var6 > 0;
   }
}
