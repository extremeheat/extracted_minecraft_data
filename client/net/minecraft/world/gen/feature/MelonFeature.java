package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class MelonFeature extends Feature<NoFeatureConfig> {
   public MelonFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      for(int var6 = 0; var6 < 64; ++var6) {
         BlockPos var7 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         IBlockState var8 = Blocks.field_150440_ba.func_176223_P();
         if (var1.func_180495_p(var7.func_177977_b()).func_177230_c() == Blocks.field_196658_i) {
            var1.func_180501_a(var7, var8, 2);
         }
      }

      return true;
   }
}
