package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class PumpkinFeature extends Feature<NoFeatureConfig> {
   public PumpkinFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      int var6 = 0;
      IBlockState var7 = Blocks.field_150423_aK.func_176223_P();

      for(int var8 = 0; var8 < 64; ++var8) {
         BlockPos var9 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.func_175623_d(var9) && var1.func_180495_p(var9.func_177977_b()).func_177230_c() == Blocks.field_196658_i) {
            var1.func_180501_a(var9, var7, 2);
            ++var6;
         }
      }

      return var6 > 0;
   }
}
