package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class WaterlilyFeature extends Feature<NoFeatureConfig> {
   public WaterlilyFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      BlockPos var7;
      for(BlockPos var6 = var4; var6.func_177956_o() > 0; var6 = var7) {
         var7 = var6.func_177977_b();
         if (!var1.func_175623_d(var7)) {
            break;
         }
      }

      for(int var10 = 0; var10 < 10; ++var10) {
         BlockPos var8 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         IBlockState var9 = Blocks.field_196651_dG.func_176223_P();
         if (var1.func_175623_d(var8) && var9.func_196955_c(var1, var8)) {
            var1.func_180501_a(var8, var9, 2);
         }
      }

      return true;
   }
}
