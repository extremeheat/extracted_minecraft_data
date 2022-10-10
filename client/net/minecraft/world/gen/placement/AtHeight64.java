package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class AtHeight64 extends BasePlacement<FrequencyConfig> {
   public AtHeight64() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, FrequencyConfig var5, Feature<C> var6, C var7) {
      for(int var8 = 0; var8 < var5.field_202476_a; ++var8) {
         int var9 = var3.nextInt(16);
         boolean var10 = true;
         int var11 = var3.nextInt(16);
         var6.func_212245_a(var1, var2, var3, var4.func_177982_a(var9, 64, var11), var7);
      }

      return true;
   }
}
