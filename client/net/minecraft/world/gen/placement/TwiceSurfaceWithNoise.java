package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class TwiceSurfaceWithNoise extends BasePlacement<NoiseDependant> {
   public TwiceSurfaceWithNoise() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoiseDependant var5, Feature<C> var6, C var7) {
      double var8 = Biome.field_180281_af.func_151601_a((double)var4.func_177958_n() / 200.0D, (double)var4.func_177952_p() / 200.0D);
      int var10 = var8 < var5.field_202473_a ? var5.field_202474_b : var5.field_202475_c;

      for(int var11 = 0; var11 < var10; ++var11) {
         int var12 = var3.nextInt(16);
         int var13 = var3.nextInt(16);
         int var14 = var1.func_205770_a(Heightmap.Type.MOTION_BLOCKING, var4.func_177982_a(var12, 0, var13)).func_177956_o() * 2;
         if (var14 > 0) {
            int var15 = var3.nextInt(var14);
            var6.func_212245_a(var1, var2, var3, var4.func_177982_a(var12, var15, var13), var7);
         }
      }

      return true;
   }
}
