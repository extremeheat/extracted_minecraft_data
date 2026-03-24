package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class AcaciaFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<AcaciaFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((i) -> foliagePlacerParts(i).apply(i, AcaciaFoliagePlacer::new));

   public AcaciaFoliagePlacer(final IntProvider radius, final IntProvider offset) {
      super(radius, offset);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.ACACIA_FOLIAGE_PLACER;
   }

   protected void createFoliage(final WorldGenLevel level, final FoliagePlacer.FoliageSetter foliageSetter, final RandomSource random, final TreeConfiguration config, final int treeHeight, final FoliagePlacer.FoliageAttachment foliageAttachment, final int foliageHeight, final int leafRadius, final int offset) {
      boolean doubleTrunk = foliageAttachment.doubleTrunk();
      BlockPos foliagePos = foliageAttachment.pos().above(offset);
      this.placeLeavesRow(level, foliageSetter, random, config, foliagePos, leafRadius + foliageAttachment.radiusOffset(), -1 - foliageHeight, doubleTrunk);
      this.placeLeavesRow(level, foliageSetter, random, config, foliagePos, leafRadius - 1, -foliageHeight, doubleTrunk);
      this.placeLeavesRow(level, foliageSetter, random, config, foliagePos, leafRadius + foliageAttachment.radiusOffset() - 1, 0, doubleTrunk);
   }

   public int foliageHeight(final RandomSource random, final int treeHeight, final TreeConfiguration config) {
      return 0;
   }

   protected boolean shouldSkipLocation(final RandomSource random, final int dx, final int y, final int dz, final int currentRadius, final boolean doubleTrunk) {
      if (y == 0) {
         return (dx > 1 || dz > 1) && dx != 0 && dz != 0;
      } else {
         return dx == currentRadius && dz == currentRadius && currentRadius > 0;
      }
   }
}
