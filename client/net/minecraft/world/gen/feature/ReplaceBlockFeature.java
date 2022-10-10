package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfig> {
   public ReplaceBlockFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, ReplaceBlockConfig var5) {
      if (var5.field_202457_a.test(var1.func_180495_p(var4))) {
         var1.func_180501_a(var4, var5.field_202458_b, 2);
      }

      return true;
   }
}
