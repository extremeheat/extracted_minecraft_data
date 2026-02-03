package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RandomPatchFeature extends Feature<RandomPatchConfiguration> {
   public RandomPatchFeature(final Codec<RandomPatchConfiguration> codec) {
      super(codec);
   }

   public boolean place(final FeaturePlaceContext<RandomPatchConfiguration> context) {
      RandomPatchConfiguration config = context.config();
      RandomSource random = context.random();
      BlockPos origin = context.origin();
      WorldGenLevel level = context.level();
      int placed = 0;
      BlockPos.MutableBlockPos grassPos = new BlockPos.MutableBlockPos();
      int xzBound = config.xzSpread() + 1;
      int yBound = config.ySpread() + 1;

      for(int i = 0; i < config.tries(); ++i) {
         grassPos.setWithOffset(origin, random.nextInt(xzBound) - random.nextInt(xzBound), random.nextInt(yBound) - random.nextInt(yBound), random.nextInt(xzBound) - random.nextInt(xzBound));
         if (((PlacedFeature)config.feature().value()).place(level, context.chunkGenerator(), random, grassPos)) {
            ++placed;
         }
      }

      return placed > 0;
   }
}
