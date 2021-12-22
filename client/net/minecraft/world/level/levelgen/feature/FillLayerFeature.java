package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;

public class FillLayerFeature extends Feature<LayerConfiguration> {
   public FillLayerFeature(Codec<LayerConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<LayerConfiguration> var1) {
      BlockPos var2 = var1.origin();
      LayerConfiguration var3 = (LayerConfiguration)var1.config();
      WorldGenLevel var4 = var1.level();
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

      for(int var6 = 0; var6 < 16; ++var6) {
         for(int var7 = 0; var7 < 16; ++var7) {
            int var8 = var2.getX() + var6;
            int var9 = var2.getZ() + var7;
            int var10 = var4.getMinBuildHeight() + var3.height;
            var5.set(var8, var10, var9);
            if (var4.getBlockState(var5).isAir()) {
               var4.setBlock(var5, var3.state, 2);
            }
         }
      }

      return true;
   }
}
