package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;

public class PoplarFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<PoplarFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((i) -> foliagePlacerParts(i).and(i.group(IntProviders.codec(5, 16).fieldOf("height").forGetter((p) -> p.height), Codec.floatRange(0.0F, 1.0F).fieldOf("side_hole_chance").forGetter((p) -> p.sideHoleChance))).apply(i, PoplarFoliagePlacer::new));
   private final Function<Integer, Integer> topFoliageLayer = (heightx) -> heightx - 1;
   private final Function<Integer, Integer> secondTopFoliageLayer = (heightx) -> heightx - 2;
   private final IntProvider height;
   private final float sideHoleChance;
   private boolean flipRhombusShape;
   private int foliageHeight;

   public PoplarFoliagePlacer(final IntProvider radius, final IntProvider offset, final IntProvider height, final float sideHoleChance) {
      super(radius, offset);
      this.height = height;
      this.sideHoleChance = sideHoleChance;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.POPLAR_FOLIAGE_PLACER;
   }

   protected void createFoliage(final WorldGenLevel level, final FoliagePlacer.FoliageSetter foliageSetter, final RandomSource random, final TreeFeature tree, final int treeHeight, final FoliagePlacer.FoliageAttachment foliageAttachment, final int foliageHeight, final int leafRadius, final int offset) {
      boolean doubleTrunk = foliageAttachment.doubleTrunk();
      BlockPos foliagePos = foliageAttachment.pos().above(offset);
      int currentRadius = leafRadius + foliageAttachment.radiusOffset() - 1;
      this.foliageHeight = foliageHeight;
      this.flipRhombusShape = random.nextBoolean();
      this.placeLeavesRow(level, foliageSetter, random, tree, foliagePos, currentRadius - 2, (Integer)this.topFoliageLayer.apply(foliageHeight), doubleTrunk);
      this.placeLeavesRow(level, foliageSetter, random, tree, foliagePos, currentRadius - 1, (Integer)this.secondTopFoliageLayer.apply(foliageHeight), doubleTrunk);
      this.placeLeavesRow(level, foliageSetter, random, tree, foliagePos, currentRadius - 1, foliageHeight - 3, doubleTrunk);

      for(int y = foliageHeight - 4; y >= 1; --y) {
         this.placeLeavesRow(level, foliageSetter, random, tree, foliagePos, currentRadius, y, doubleTrunk);
      }

      this.replaceLeavesWithLog(level, foliageSetter, tree, random, foliagePos, currentRadius, foliageHeight - 4, doubleTrunk);
      this.placeLeavesRow(level, foliageSetter, random, tree, foliagePos, currentRadius - 1, 0, doubleTrunk);
      this.placeLeavesRow(level, foliageSetter, random, tree, foliagePos, Mth.clamp(currentRadius - 2, 1, 2), -1, doubleTrunk);
   }

   private void replaceLeavesWithLog(final WorldGenLevel level, final FoliagePlacer.FoliageSetter foliageSetter, final TreeFeature tree, final RandomSource random, final BlockPos origin, final int currentRadius, final int y, final boolean doubleTrunk) {
      int offset = doubleTrunk ? 1 : 0;
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

      for(int dx = -currentRadius; dx <= currentRadius + offset; ++dx) {
         for(int dz = -currentRadius; dz <= currentRadius + offset; ++dz) {
            int absDz = Mth.abs(dz);
            int absDx = Mth.abs(dx);
            if (isWithinRhombusShape(currentRadius, absDx, absDz, this.getCornerBlocksToCutForRhombusShape(dx, dz, currentRadius, this.shouldRowBePartialRhombusShape(y)), 2) && (absDz == 0 && currentRadius - absDx >= 4 || absDx == 0 && currentRadius - absDz >= 4)) {
               pos.setWithOffset(origin, dx, y, dz);
               tryPlaceLog(level, foliageSetter, random, tree, pos, getSidewaysStateModifier(Direction.fromAxisAndDirection(absDz == 0 ? Direction.Axis.X : Direction.Axis.Z, Direction.AxisDirection.POSITIVE)));
            }
         }
      }

   }

   private static void tryPlaceLog(final WorldGenLevel level, final FoliagePlacer.FoliageSetter foliageSetter, final RandomSource random, final TreeFeature tree, final BlockPos pos, final Function<BlockState, BlockState> stateModifier) {
      if (level.isStateAtPosition(pos, (state) -> state.equals(tree.foliageProvider().getState(level, random, pos)))) {
         foliageSetter.set(pos, (BlockState)stateModifier.apply(tree.trunkProvider().getState(level, random, pos)));
      }

   }

   private static Function<BlockState, BlockState> getSidewaysStateModifier(final Direction branchDirection) {
      return (state) -> (BlockState)state.trySetValue(RotatedPillarBlock.AXIS, branchDirection.getAxis());
   }

   public int foliageHeight(final RandomSource random, final int treeHeight, final TreeFeature tree) {
      return this.height.sample(random);
   }

   protected boolean shouldSkipLocationSigned(final RandomSource random, final int dx, final int y, final int dz, final int currentRadius, final boolean doubleTrunk) {
      return this.shouldSkipLocation(random, dx, y, dz, currentRadius, doubleTrunk);
   }

   protected boolean shouldSkipLocation(final RandomSource random, final int dx, final int y, final int dz, final int currentRadius, final boolean doubleTrunk) {
      boolean shouldRowBePartialRhombusShape = this.shouldRowBePartialRhombusShape(y);
      int cornerBlocksToCutForRhombusShape = this.getCornerBlocksToCutForRhombusShape(dx, dz, currentRadius, shouldRowBePartialRhombusShape);
      int absDx = Mth.abs(dx);
      int absDz = Mth.abs(dz);
      boolean isRhombusEdgeBlock = absDx == currentRadius || absDz == currentRadius;
      if (shouldRowBePartialRhombusShape && isRhombusEdgeBlock) {
         return true;
      } else {
         int additionalSideRemoval = random.nextFloat() <= this.sideHoleChance ? 1 : 0;
         return !isWithinRhombusShape(currentRadius, absDx, absDz, cornerBlocksToCutForRhombusShape, additionalSideRemoval);
      }
   }

   private int getCornerBlocksToCutForRhombusShape(final int dx, final int dz, final int currentRadius, final boolean shouldRowBePartialRhombusShape) {
      boolean isSmallCornerOfShape = this.flipRhombusShape ? isLeftTopCornerOrRightLowerCorner(dx, dz) : isLeftLowerCornerOrRightTopCorner(dx, dz);
      return isSmallCornerOfShape ? currentRadius - 1 : (shouldRowBePartialRhombusShape ? currentRadius + 1 : currentRadius);
   }

   private static boolean isWithinRhombusShape(final int currentRadius, final int absDx, final int absDz, final int cornerBlocksToCutForRhombusShape, final int additionalSideRemoval) {
      return absDx + absDz <= currentRadius * 2 - (cornerBlocksToCutForRhombusShape + additionalSideRemoval);
   }

   private static boolean isLeftLowerCornerOrRightTopCorner(final int dx, final int dz) {
      return dx > 0 && dz < 0 || dz > 0 && dx < 0;
   }

   private static boolean isLeftTopCornerOrRightLowerCorner(final int dx, final int dz) {
      return dx > 0 && dz > 0 || dz < 0 && dx < 0;
   }

   private boolean shouldRowBePartialRhombusShape(final int y) {
      return (Integer)this.topFoliageLayer.apply(this.foliageHeight) == y || (Integer)this.secondTopFoliageLayer.apply(this.foliageHeight) == y;
   }
}
