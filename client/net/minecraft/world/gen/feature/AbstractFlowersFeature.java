package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public abstract class AbstractFlowersFeature extends Feature<NoFeatureConfig> {
   public AbstractFlowersFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      IBlockState var6 = this.func_202355_a(var3, var4);
      int var7 = 0;

      for(int var8 = 0; var8 < 64; ++var8) {
         BlockPos var9 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.func_175623_d(var9) && var9.func_177956_o() < 255 && var6.func_196955_c(var1, var9)) {
            var1.func_180501_a(var9, var6, 2);
            ++var7;
         }
      }

      return var7 > 0;
   }

   public abstract IBlockState func_202355_a(Random var1, BlockPos var2);
}
