package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;

public class DripstoneClusterFeature extends Feature<DripstoneClusterConfiguration> {
   public DripstoneClusterFeature(final Codec<DripstoneClusterConfiguration> codec) {
      super(codec);
   }

   public boolean place(final FeaturePlaceContext<DripstoneClusterConfiguration> context) {
      WorldGenLevel level = context.level();
      BlockPos origin = context.origin();
      DripstoneClusterConfiguration config = context.config();
      RandomSource random = context.random();
      if (!DripstoneUtils.isEmptyOrWater(level, origin)) {
         return false;
      } else {
         int height = config.height.sample(random);
         float wetness = config.wetness.sample(random);
         float density = config.density.sample(random);
         int xRadius = config.radius.sample(random);
         int zRadius = config.radius.sample(random);

         for(int dx = -xRadius; dx <= xRadius; ++dx) {
            for(int dz = -zRadius; dz <= zRadius; ++dz) {
               double chanceOfStalagmiteOrStalactite = this.getChanceOfStalagmiteOrStalactite(xRadius, zRadius, dx, dz, config);
               BlockPos pos = origin.offset(dx, 0, dz);
               this.placeColumn(level, random, pos, dx, dz, wetness, chanceOfStalagmiteOrStalactite, height, density, config);
            }
         }

         return true;
      }
   }

   private void placeColumn(final WorldGenLevel level, final RandomSource random, final BlockPos pos, final int dx, final int dz, final float chanceOfWater, final double chanceOfStalagmiteOrStalactite, final int clusterHeight, final float density, final DripstoneClusterConfiguration config) {
      Optional<Column> baseColumn = Column.scan(level, pos, config.floorToCeilingSearchRange, DripstoneUtils::isEmptyOrWater, DripstoneUtils::isNeitherEmptyNorWater);
      if (!baseColumn.isEmpty()) {
         OptionalInt ceiling = ((Column)baseColumn.get()).getCeiling();
         OptionalInt baseFloor = ((Column)baseColumn.get()).getFloor();
         if (!ceiling.isEmpty() || !baseFloor.isEmpty()) {
            boolean wantPool = random.nextFloat() < chanceOfWater;
            Column column;
            if (wantPool && baseFloor.isPresent() && this.canPlacePool(level, pos.atY(baseFloor.getAsInt()))) {
               int baseFloorY = baseFloor.getAsInt();
               column = ((Column)baseColumn.get()).withFloor(OptionalInt.of(baseFloorY - 1));
               level.setBlock(pos.atY(baseFloorY), Blocks.WATER.defaultBlockState(), 2);
            } else {
               column = (Column)baseColumn.get();
            }

            OptionalInt floor = column.getFloor();
            boolean wantStalactite = random.nextDouble() < chanceOfStalagmiteOrStalactite;
            int stalactiteHeight;
            if (ceiling.isPresent() && wantStalactite && !this.isLava(level, pos.atY(ceiling.getAsInt()))) {
               int ceilingThickness = config.dripstoneBlockLayerThickness.sample(random);
               this.replaceBlocksWithDripstoneBlocks(level, pos.atY(ceiling.getAsInt()), ceilingThickness, Direction.UP);
               int maxHeightForThisColumn;
               if (floor.isPresent()) {
                  maxHeightForThisColumn = Math.min(clusterHeight, ceiling.getAsInt() - floor.getAsInt());
               } else {
                  maxHeightForThisColumn = clusterHeight;
               }

               stalactiteHeight = this.getDripstoneHeight(random, dx, dz, density, maxHeightForThisColumn, config);
            } else {
               stalactiteHeight = 0;
            }

            boolean wantStalagmite = random.nextDouble() < chanceOfStalagmiteOrStalactite;
            int stalagmiteHeight;
            if (floor.isPresent() && wantStalagmite && !this.isLava(level, pos.atY(floor.getAsInt()))) {
               int floorThickness = config.dripstoneBlockLayerThickness.sample(random);
               this.replaceBlocksWithDripstoneBlocks(level, pos.atY(floor.getAsInt()), floorThickness, Direction.DOWN);
               if (ceiling.isPresent()) {
                  stalagmiteHeight = Math.max(0, stalactiteHeight + Mth.randomBetweenInclusive(random, -config.maxStalagmiteStalactiteHeightDiff, config.maxStalagmiteStalactiteHeightDiff));
               } else {
                  stalagmiteHeight = this.getDripstoneHeight(random, dx, dz, density, clusterHeight, config);
               }
            } else {
               stalagmiteHeight = 0;
            }

            int actualStalagmiteHeight;
            int actualStalactiteHeight;
            if (ceiling.isPresent() && floor.isPresent() && ceiling.getAsInt() - stalactiteHeight <= floor.getAsInt() + stalagmiteHeight) {
               int floorY = floor.getAsInt();
               int ceilingY = ceiling.getAsInt();
               int lowestStalactiteBottom = Math.max(ceilingY - stalactiteHeight, floorY + 1);
               int highestStalagmiteTop = Math.min(floorY + stalagmiteHeight, ceilingY - 1);
               int actualStalactiteBottom = Mth.randomBetweenInclusive(random, lowestStalactiteBottom, highestStalagmiteTop + 1);
               int actualStalagmiteTop = actualStalactiteBottom - 1;
               actualStalactiteHeight = ceilingY - actualStalactiteBottom;
               actualStalagmiteHeight = actualStalagmiteTop - floorY;
            } else {
               actualStalactiteHeight = stalactiteHeight;
               actualStalagmiteHeight = stalagmiteHeight;
            }

            boolean mergeTips = random.nextBoolean() && actualStalactiteHeight > 0 && actualStalagmiteHeight > 0 && column.getHeight().isPresent() && actualStalactiteHeight + actualStalagmiteHeight == column.getHeight().getAsInt();
            if (ceiling.isPresent()) {
               DripstoneUtils.growPointedDripstone(level, pos.atY(ceiling.getAsInt() - 1), Direction.DOWN, actualStalactiteHeight, mergeTips);
            }

            if (floor.isPresent()) {
               DripstoneUtils.growPointedDripstone(level, pos.atY(floor.getAsInt() + 1), Direction.UP, actualStalagmiteHeight, mergeTips);
            }

         }
      }
   }

   private boolean isLava(final LevelReader level, final BlockPos pos) {
      return level.getBlockState(pos).is(Blocks.LAVA);
   }

   private int getDripstoneHeight(final RandomSource random, final int dx, final int dz, final float density, final int maxHeight, final DripstoneClusterConfiguration config) {
      if (random.nextFloat() > density) {
         return 0;
      } else {
         int distanceFromCenter = Math.abs(dx) + Math.abs(dz);
         float heightMean = (float)Mth.clampedMap((double)distanceFromCenter, 0.0, (double)config.maxDistanceFromCenterAffectingHeightBias, (double)maxHeight / 2.0, 0.0);
         return (int)randomBetweenBiased(random, 0.0F, (float)maxHeight, heightMean, (float)config.heightDeviation);
      }
   }

   private boolean canPlacePool(final WorldGenLevel level, final BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      if (!state.is(Blocks.WATER) && !state.is(Blocks.DRIPSTONE_BLOCK) && !state.is(Blocks.POINTED_DRIPSTONE)) {
         if (level.getBlockState(pos.above()).getFluidState().is(FluidTags.WATER)) {
            return false;
         } else {
            for(Direction direction : Direction.Plane.HORIZONTAL) {
               if (!this.canBeAdjacentToWater(level, pos.relative(direction))) {
                  return false;
               }
            }

            return this.canBeAdjacentToWater(level, pos.below());
         }
      } else {
         return false;
      }
   }

   private boolean canBeAdjacentToWater(final LevelAccessor level, final BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.getFluidState().is(FluidTags.WATER);
   }

   private void replaceBlocksWithDripstoneBlocks(final WorldGenLevel level, final BlockPos firstPos, final int maxCount, final Direction direction) {
      BlockPos.MutableBlockPos pos = firstPos.mutable();

      for(int i = 0; i < maxCount; ++i) {
         if (!DripstoneUtils.placeDripstoneBlockIfPossible(level, pos)) {
            return;
         }

         pos.move(direction);
      }

   }

   private double getChanceOfStalagmiteOrStalactite(final int xRadius, final int zRadius, final int dx, final int dz, final DripstoneClusterConfiguration config) {
      int xDistanceFromEdge = xRadius - Math.abs(dx);
      int zDistanceFromEdge = zRadius - Math.abs(dz);
      int distanceFromEdge = Math.min(xDistanceFromEdge, zDistanceFromEdge);
      return (double)Mth.clampedMap((float)distanceFromEdge, 0.0F, (float)config.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn, config.chanceOfDripstoneColumnAtMaxDistanceFromCenter, 1.0F);
   }

   private static float randomBetweenBiased(final RandomSource random, final float min, final float maxExclusive, final float mean, final float deviation) {
      return ClampedNormalFloat.sample(random, mean, deviation, min, maxExclusive);
   }
}
