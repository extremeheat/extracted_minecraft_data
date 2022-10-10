package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class TwoFeatureChoiceFeature extends Feature<TwoFeatureChoiceConfig> {
   public TwoFeatureChoiceFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, TwoFeatureChoiceConfig var5) {
      boolean var6 = var3.nextBoolean();
      return var6 ? this.func_202360_a(var5.field_202445_a, var5.field_202446_b, var1, var2, var3, var4) : this.func_202360_a(var5.field_202447_c, var5.field_202448_d, var1, var2, var3, var4);
   }

   <FC extends IFeatureConfig> boolean func_202360_a(Feature<FC> var1, IFeatureConfig var2, IWorld var3, IChunkGenerator<? extends IChunkGenSettings> var4, Random var5, BlockPos var6) {
      return var1.func_212245_a(var3, var4, var5, var6, var2);
   }
}
