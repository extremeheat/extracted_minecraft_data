package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfiguration> {
   public ReplaceBlockFeature(Codec<ReplaceBlockConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      ReplaceBlockConfiguration var4 = (ReplaceBlockConfiguration)var1.config();

      for (OreConfiguration.TargetBlockState var6 : var4.targetStates) {
         if (var6.target.test(var2.getBlockState(var3), var1.random())) {
            var2.setBlock(var3, var6.state, 2);
            break;
         }
      }

      return true;
   }
}
