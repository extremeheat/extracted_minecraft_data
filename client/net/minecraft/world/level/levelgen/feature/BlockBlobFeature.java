package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class BlockBlobFeature extends Feature<BlockStateConfiguration> {
   public BlockBlobFeature(final Codec<BlockStateConfiguration> codec) {
      super(codec);
   }

   public boolean place(final FeaturePlaceContext<BlockStateConfiguration> context) {
      BlockPos origin = context.origin();
      WorldGenLevel level = context.level();
      RandomSource random = context.random();

      BlockStateConfiguration config;
      for(config = context.config(); origin.getY() > level.getMinY() + 3; origin = origin.below()) {
         if (!level.isEmptyBlock(origin.below())) {
            BlockState subState = level.getBlockState(origin.below());
            if (isDirt(subState) || isStone(subState)) {
               break;
            }
         }
      }

      if (origin.getY() <= level.getMinY() + 3) {
         return false;
      } else {
         for(int c = 0; c < 3; ++c) {
            int xr = random.nextInt(2);
            int yr = random.nextInt(2);
            int zr = random.nextInt(2);
            float tr = (float)(xr + yr + zr) * 0.333F + 0.5F;

            for(BlockPos blockPos : BlockPos.betweenClosed(origin.offset(-xr, -yr, -zr), origin.offset(xr, yr, zr))) {
               if (blockPos.distSqr(origin) <= (double)(tr * tr)) {
                  level.setBlock(blockPos, config.state, 3);
               }
            }

            origin = origin.offset(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
         }

         return true;
      }
   }
}
