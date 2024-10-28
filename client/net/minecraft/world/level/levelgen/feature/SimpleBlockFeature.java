package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class SimpleBlockFeature extends Feature<SimpleBlockConfiguration> {
   public SimpleBlockFeature(Codec<SimpleBlockConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> var1) {
      SimpleBlockConfiguration var2 = (SimpleBlockConfiguration)var1.config();
      WorldGenLevel var3 = var1.level();
      BlockPos var4 = var1.origin();
      BlockState var5 = var2.toPlace().getState(var1.random(), var4);
      if (var5.canSurvive(var3, var4)) {
         if (var5.getBlock() instanceof DoublePlantBlock) {
            if (!var3.isEmptyBlock(var4.above())) {
               return false;
            }

            DoublePlantBlock.placeAt(var3, var5, var4, 2);
         } else {
            var3.setBlock(var4, var5, 2);
         }

         return true;
      } else {
         return false;
      }
   }
}
