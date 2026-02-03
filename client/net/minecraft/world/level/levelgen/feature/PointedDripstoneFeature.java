package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;

public class PointedDripstoneFeature extends Feature<PointedDripstoneConfiguration> {
   public PointedDripstoneFeature(final Codec<PointedDripstoneConfiguration> codec) {
      super(codec);
   }

   public boolean place(final FeaturePlaceContext<PointedDripstoneConfiguration> context) {
      LevelAccessor level = context.level();
      BlockPos pos = context.origin();
      RandomSource random = context.random();
      PointedDripstoneConfiguration config = context.config();
      Optional<Direction> tipDirection = getTipDirection(level, pos, random);
      if (tipDirection.isEmpty()) {
         return false;
      } else {
         BlockPos rootPos = pos.relative(((Direction)tipDirection.get()).getOpposite());
         createPatchOfDripstoneBlocks(level, random, rootPos, config);
         int height = random.nextFloat() < config.chanceOfTallerDripstone && DripstoneUtils.isEmptyOrWater(level.getBlockState(pos.relative((Direction)tipDirection.get()))) ? 2 : 1;
         DripstoneUtils.growPointedDripstone(level, pos, (Direction)tipDirection.get(), height, false);
         return true;
      }
   }

   private static Optional<Direction> getTipDirection(final LevelAccessor level, final BlockPos pos, final RandomSource random) {
      boolean canPlaceAbove = DripstoneUtils.isDripstoneBase(level.getBlockState(pos.above()));
      boolean canPlaceBelow = DripstoneUtils.isDripstoneBase(level.getBlockState(pos.below()));
      if (canPlaceAbove && canPlaceBelow) {
         return Optional.of(random.nextBoolean() ? Direction.DOWN : Direction.UP);
      } else if (canPlaceAbove) {
         return Optional.of(Direction.DOWN);
      } else {
         return canPlaceBelow ? Optional.of(Direction.UP) : Optional.empty();
      }
   }

   private static void createPatchOfDripstoneBlocks(final LevelAccessor level, final RandomSource random, final BlockPos pos, final PointedDripstoneConfiguration config) {
      DripstoneUtils.placeDripstoneBlockIfPossible(level, pos);

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (!(random.nextFloat() > config.chanceOfDirectionalSpread)) {
            BlockPos pos1 = pos.relative(direction);
            DripstoneUtils.placeDripstoneBlockIfPossible(level, pos1);
            if (!(random.nextFloat() > config.chanceOfSpreadRadius2)) {
               BlockPos pos2 = pos1.relative(Direction.getRandom(random));
               DripstoneUtils.placeDripstoneBlockIfPossible(level, pos2);
               if (!(random.nextFloat() > config.chanceOfSpreadRadius3)) {
                  BlockPos pos3 = pos2.relative(Direction.getRandom(random));
                  DripstoneUtils.placeDripstoneBlockIfPossible(level, pos3);
               }
            }
         }
      }

   }
}
