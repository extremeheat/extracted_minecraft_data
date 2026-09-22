package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class StraightTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<StraightTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((i) -> trunkPlacerParts(i).and(IntProviders.POSITIVE_CODEC.optionalFieldOf("trunk_width", new ConstantInt(1)).forGetter((c) -> c.trunkWidth)).apply(i, StraightTrunkPlacer::new));
   private final IntProvider trunkWidth;

   public StraightTrunkPlacer(final int baseHeight, final int heightRandA, final int heightRandB) {
      super(baseHeight, heightRandA, heightRandB);
      this.trunkWidth = new ConstantInt(1);
   }

   public StraightTrunkPlacer(final int baseHeight, final int heightRandA, final int heightRandB, final IntProvider trunkWidth) {
      super(baseHeight, heightRandA, heightRandB);
      this.trunkWidth = trunkWidth;
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(final WorldGenLevel level, final BiConsumer<BlockPos, BlockState> trunkSetter, final RandomSource random, final int treeHeight, final BlockPos origin, final TreeFeature tree) {
      int trunkWidth = this.trunkWidth.sample(random);
      int distToNorthWestCorner = (trunkWidth - 1) / 2;
      int distToSouthEastCorner = trunkWidth / 2;
      BlockPos northWestCorner = origin.relative(Direction.NORTH, distToNorthWestCorner).relative(Direction.WEST, distToNorthWestCorner);
      BlockPos southEastCorner = origin.relative(Direction.SOUTH, distToSouthEastCorner).relative(Direction.EAST, distToSouthEastCorner);

      for(int y = 0; y < treeHeight; ++y) {
         for(BlockPos pos : BlockPos.betweenClosed(northWestCorner, southEastCorner)) {
            this.placeLog(level, trunkSetter, random, pos.above(y), tree);
         }
      }

      return ImmutableList.of(new FoliagePlacer.FoliageAttachment(northWestCorner.above(treeHeight), 0, trunkWidth, trunkWidth));
   }
}
