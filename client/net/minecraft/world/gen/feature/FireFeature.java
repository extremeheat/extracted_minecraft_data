package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class FireFeature extends Feature<NoFeatureConfig> {
   public FireFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      for(int var6 = 0; var6 < 64; ++var6) {
         BlockPos var7 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.func_175623_d(var7) && var1.func_180495_p(var7.func_177977_b()).func_177230_c() == Blocks.field_150424_aL) {
            var1.func_180501_a(var7, Blocks.field_150480_ab.func_176223_P(), 2);
         }
      }

      return true;
   }
}
