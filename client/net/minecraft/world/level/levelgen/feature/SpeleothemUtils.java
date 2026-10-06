package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;

public class SpeleothemUtils {
   public SpeleothemUtils() {
      super();
   }

   protected static double getSpeleothemHeight(double xzDistanceFromCenter, final double speleothemRadius, final double scale, final double bluntness) {
      if (xzDistanceFromCenter < bluntness) {
         xzDistanceFromCenter = bluntness;
      }

      double cutoff = 0.384;
      double r = xzDistanceFromCenter / speleothemRadius * 0.384;
      double part1 = 0.75 * Math.pow(r, 1.3333333333333333);
      double part2 = Math.pow(r, 0.6666666666666666);
      double part3 = 0.3333333333333333 * Math.log(r);
      double heightRelativeToMaxRadius = scale * (part1 - part2 - part3);
      heightRelativeToMaxRadius = Math.max(heightRelativeToMaxRadius, 0.0);
      return heightRelativeToMaxRadius / 0.384 * speleothemRadius;
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
      return level.isStateAtPosition(pos, SpeleothemUtils::isEmptyOrWater);
   }

   protected static boolean isEmptyOrWaterOrLava(final LevelAccessor level, final BlockPos pos) {
      return level.isStateAtPosition(pos, SpeleothemUtils::isEmptyOrWaterOrLava);
   }

   protected static void buildBaseToTipColumn(final Direction direction, final int totalLength, final boolean mergedTip, final Consumer<BlockState> consumer, final Block pointedBlock) {
      if (totalLength >= 3) {
         consumer.accept(createPointedBlock(direction, SpeleothemThickness.BASE, pointedBlock));

         for(int i = 0; i < totalLength - 3; ++i) {
            consumer.accept(createPointedBlock(direction, SpeleothemThickness.MIDDLE, pointedBlock));
         }
      }

      if (totalLength >= 2) {
         consumer.accept(createPointedBlock(direction, SpeleothemThickness.FRUSTUM, pointedBlock));
      }

      if (totalLength >= 1) {
         consumer.accept(createPointedBlock(direction, mergedTip ? SpeleothemThickness.TIP_MERGE : SpeleothemThickness.TIP, pointedBlock));
      }

   }

   protected static void buildBaseToTipColumn(final Direction direction, final int totalLength, final boolean mergedTip, final Consumer<BlockState> consumer, final Block pointedBlock, final BaseBlockTransformer baseBlockTransformer) {
      for(int remainingLength = totalLength; remainingLength > 0; --remainingLength) {
         boolean isColumnBase = remainingLength == totalLength;
         SpeleothemThickness thickness;
         if (remainingLength == 2) {
            thickness = SpeleothemThickness.FRUSTUM;
         } else if (remainingLength == 1) {
            thickness = mergedTip ? SpeleothemThickness.TIP_MERGE : SpeleothemThickness.TIP;
         } else if (isColumnBase) {
            thickness = SpeleothemThickness.BASE;
         } else {
            thickness = SpeleothemThickness.MIDDLE;
         }

         BlockState state = createPointedBlock(direction, thickness, pointedBlock);
         consumer.accept(isColumnBase ? baseBlockTransformer.transform(state) : state);
      }

   }

   protected static void growSpeleothem(final LevelAccessor level, final BlockPos startPos, final Direction tipDirection, final int height, final boolean mergedTip, final Block baseBlock, final Block pointedBlock, final HolderSet<Block> replaceableBlocks) {
      growSpeleothem(level, startPos, tipDirection, height, mergedTip, baseBlock, pointedBlock, replaceableBlocks, SpeleothemUtils.BaseBlockTransformer.NONE);
   }

   protected static void growSpeleothem(final LevelAccessor level, final BlockPos startPos, final Direction tipDirection, final int height, final boolean mergedTip, final Block baseBlock, final Block pointedBlock, final HolderSet<Block> replaceableBlocks, final BaseBlockTransformer baseBlockTransformer) {
      if (isBase(level.getBlockState(startPos.relative(tipDirection.getOpposite())), baseBlock, replaceableBlocks)) {
         BlockPos.MutableBlockPos pos = startPos.mutable();
         buildBaseToTipColumn(tipDirection, height, mergedTip, (state) -> {
            if (state.is(pointedBlock)) {
               state = (BlockState)state.setValue(PointedDripstoneBlock.WATERLOGGED, level.isWaterAt(pos));
            }

            level.setBlock(pos, state, 2);
            pos.move(tipDirection);
         }, pointedBlock, baseBlockTransformer);
      }
   }

   protected static boolean placeBaseBlockIfPossible(final LevelAccessor level, final BlockPos pos, final Block baseBlock, final HolderSet<Block> replaceableBlocks) {
      BlockState state = level.getBlockState(pos);
      if (state.is(replaceableBlocks)) {
         level.setBlock(pos, baseBlock.defaultBlockState(), 2);
         return true;
      } else {
         return false;
      }
   }

   private static BlockState createPointedBlock(final Direction direction, final SpeleothemThickness thickness, final Block pointedBlock) {
      return (BlockState)((BlockState)pointedBlock.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)).setValue(PointedDripstoneBlock.THICKNESS, thickness);
   }

   public static boolean isBaseOrLava(final BlockState state, final Block baseBlock, final HolderSet<Block> replaceableBlocks) {
      return isBase(state, baseBlock, replaceableBlocks) || state.is(Blocks.LAVA);
   }

   public static boolean isBase(final BlockState state, final Block baseBlock, final HolderSet<Block> replaceableBlocks) {
      return state.is(baseBlock) || state.is(replaceableBlocks);
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

   public static enum BaseBlockTransformer implements StringRepresentable {
      NONE("none", UnaryOperator.identity()),
      SET_ATTACHED("set_attached", (state) -> (BlockState)state.trySetValue(BlockStateProperties.ATTACHED, true));

      public static final Codec<BaseBlockTransformer> CODEC = StringRepresentable.<BaseBlockTransformer>fromEnum(BaseBlockTransformer::values);
      private final String name;
      private final UnaryOperator<BlockState> transformer;

      private BaseBlockTransformer(final String name, final UnaryOperator<BlockState> transformer) {
         this.name = name;
         this.transformer = transformer;
      }

      public BlockState transform(final BlockState state) {
         return (BlockState)this.transformer.apply(state);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static BaseBlockTransformer[] $values() {
         return new BaseBlockTransformer[]{NONE, SET_ATTACHED};
      }
   }
}
