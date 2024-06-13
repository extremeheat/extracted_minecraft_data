package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class VinesFeature extends Feature<NoneFeatureConfiguration> {
   public VinesFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      var1.config();
      if (!var2.isEmptyBlock(var3)) {
         return false;
      } else {
         for (Direction var7 : Direction.values()) {
            if (var7 != Direction.DOWN && VineBlock.isAcceptableNeighbour(var2, var3.relative(var7), var7)) {
               var2.setBlock(var3, Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(var7), Boolean.valueOf(true)), 2);
               return true;
            }
         }

         return false;
      }
   }
}
