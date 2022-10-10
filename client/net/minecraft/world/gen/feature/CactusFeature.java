package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class CactusFeature extends Feature<NoFeatureConfig> {
   public CactusFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      for(int var6 = 0; var6 < 10; ++var6) {
         BlockPos var7 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.func_175623_d(var7)) {
            int var8 = 1 + var3.nextInt(var3.nextInt(3) + 1);

            for(int var9 = 0; var9 < var8; ++var9) {
               if (Blocks.field_150434_aF.func_176223_P().func_196955_c(var1, var7)) {
                  var1.func_180501_a(var7.func_177981_b(var9), Blocks.field_150434_aF.func_176223_P(), 2);
               }
            }
         }
      }

      return true;
   }
}
