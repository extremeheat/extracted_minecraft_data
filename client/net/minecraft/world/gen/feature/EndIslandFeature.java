package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class EndIslandFeature extends Feature<NoFeatureConfig> {
   public EndIslandFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      float var6 = (float)(var3.nextInt(3) + 4);

      for(int var7 = 0; var6 > 0.5F; --var7) {
         for(int var8 = MathHelper.func_76141_d(-var6); var8 <= MathHelper.func_76123_f(var6); ++var8) {
            for(int var9 = MathHelper.func_76141_d(-var6); var9 <= MathHelper.func_76123_f(var6); ++var9) {
               if ((float)(var8 * var8 + var9 * var9) <= (var6 + 1.0F) * (var6 + 1.0F)) {
                  this.func_202278_a(var1, var4.func_177982_a(var8, var7, var9), Blocks.field_150377_bs.func_176223_P());
               }
            }
         }

         var6 = (float)((double)var6 - ((double)var3.nextInt(2) + 0.5D));
      }

      return true;
   }
}
