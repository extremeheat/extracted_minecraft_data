package net.minecraft.world.level.levelgen.feature;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;

public class DripstoneUtils {
   public DripstoneUtils() {
      super();
   }

   protected static double getDripstoneHeight(double xzDistanceFromCenter, final double dripstoneRadius, final double scale, final double bluntness) {
      if (xzDistanceFromCenter < bluntness) {
         xzDistanceFromCenter = bluntness;
      }

      double cutoff = 0.384;
      double r = xzDistanceFromCenter / dripstoneRadius * 0.384;
      double part1 = 0.75 * Math.pow(r, 1.3333333333333333);
      double part2 = Math.pow(r, 0.6666666666666666);
      double part3 = 0.3333333333333333 * Math.log(r);
      double heightRelativeToMaxRadius = scale * (part1 - part2 - part3);
      heightRelativeToMaxRadius = Math.max(heightRelativeToMaxRadius, 0.0);
      return heightRelativeToMaxRadius / 0.384 * dripstoneRadius;
   }

   protected static boolean isCircleMostlyEmbeddedInStone(final WorldGenLevel level, final BlockPos center, final int xzRadius) {
      if (isEmptyOrWaterOrLava(level, center)) {
         return false;
      } else {
         float arcLength = 6.0F;
         float angleIncrement = 6.0F / (float)xzRadius;

         for(float angle = 0.0F; angle < 6.2831855F; angle += angleIncrement) {
            int dx = (int)(Mth.cos((double)angle) * (float)xzRadius);
            int dz = (int)(Mth.sin((double)angle) * (float)xzRadius);
            if (isEmptyOrWaterOrLava(level, center.offset(dx, 0, dz))) {
               return false;
            }
         }

         return true;
      }
   }

   protected static boolean isEmptyOrWater(final LevelAccessor level, final BlockPos pos) {
      return level.isStateAtPosition(pos, DripstoneUtils::isEmptyOrWater);
   }

   protected static boolean isEmptyOrWaterOrLava(final LevelAccessor level, final BlockPos pos) {
      return level.isStateAtPosition(pos, DripstoneUtils::isEmptyOrWaterOrLava);
   }

   protected static void buildBaseToTipColumn(final Direction direction, final int totalLength, final boolean mergedTip, final Consumer<BlockState> consumer) {
      if (totalLength >= 3) {
         consumer.accept(createPointedDripstone(direction, DripstoneThickness.BASE));

         for(int i = 0; i < totalLength - 3; ++i) {
            consumer.accept(createPointedDripstone(direction, DripstoneThickness.MIDDLE));
         }
      }

      if (totalLength >= 2) {
         consumer.accept(createPointedDripstone(direction, DripstoneThickness.FRUSTUM));
      }

      if (totalLength >= 1) {
         consumer.accept(createPointedDripstone(direction, mergedTip ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
      }

   }

   protected static void growPointedDripstone(final LevelAccessor level, final BlockPos startPos, final Direction tipDirection, final int height, final boolean mergedTip) {
      if (isDripstoneBase(level.getBlockState(startPos.relative(tipDirection.getOpposite())))) {
         BlockPos.MutableBlockPos pos = startPos.mutable();
         buildBaseToTipColumn(tipDirection, height, mergedTip, (state) -> {
            if (state.is(Blocks.POINTED_DRIPSTONE)) {
               state = (BlockState)state.setValue(PointedDripstoneBlock.WATERLOGGED, level.isWaterAt(pos));
            }

            level.setBlock(pos, state, 2);
            pos.move(tipDirection);
         });
      }
   }

   protected static boolean placeDripstoneBlockIfPossible(final LevelAccessor level, final BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      if (state.is(BlockTags.DRIPSTONE_REPLACEABLE)) {
         level.setBlock(pos, Blocks.DRIPSTONE_BLOCK.defaultBlockState(), 2);
         return true;
      } else {
         return false;
      }
   }

   private static BlockState createPointedDripstone(final Direction direction, final DripstoneThickness thickness) {
      return (BlockState)((BlockState)Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)).setValue(PointedDripstoneBlock.THICKNESS, thickness);
   }

   public static boolean isDripstoneBaseOrLava(final BlockState state) {
      return isDripstoneBase(state) || state.is(Blocks.LAVA);
   }

   public static boolean isDripstoneBase(final BlockState state) {
      return state.is(Blocks.DRIPSTONE_BLOCK) || state.is(BlockTags.DRIPSTONE_REPLACEABLE);
   }

   public static boolean isEmptyOrWater(final BlockState state) {
      return state.isAir() || state.is(Blocks.WATER);
   }

   public static boolean isNeitherEmptyNorWater(final BlockState state) {
      return !state.isAir() && !state.is(Blocks.WATER);
   }

   public static boolean isEmptyOrWaterOrLava(final BlockState state) {
      return state.isAir() || state.is(Blocks.WATER) || state.is(Blocks.LAVA);
   }
}
