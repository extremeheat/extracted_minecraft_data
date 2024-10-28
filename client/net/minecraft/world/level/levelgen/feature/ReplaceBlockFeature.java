package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfiguration> {
   public ReplaceBlockFeature(Codec<ReplaceBlockConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      ReplaceBlockConfiguration var4 = (ReplaceBlockConfiguration)var1.config();
      Iterator var5 = var4.targetStates.iterator();

      while(var5.hasNext()) {
         OreConfiguration.TargetBlockState var6 = (OreConfiguration.TargetBlockState)var5.next();
         if (var6.target.test(var2.getBlockState(var3), var1.random())) {
            var2.setBlock(var3, var6.state, 2);
            break;
         }
      }

      return true;
   }
}
