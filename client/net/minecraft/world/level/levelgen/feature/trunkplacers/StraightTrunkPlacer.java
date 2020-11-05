package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StraightTrunkPlacer extends TrunkPlacer {
   public static final Codec<StraightTrunkPlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return trunkPlacerParts(var0).apply(var0, StraightTrunkPlacer::new);
   });

   public StraightTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BoundingBox var6, TreeConfiguration var7) {
      setDirtAt(var1, var4.below());

      for(int var8 = 0; var8 < var3; ++var8) {
         placeLog(var1, var2, var4.above(var8), var5, var6, var7);
      }

      return ImmutableList.of(new FoliagePlacer.FoliageAttachment(var4.above(var3), 0, false));
   }
}
