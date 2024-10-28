package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class StraightTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<StraightTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return trunkPlacerParts(var0).apply(var0, StraightTrunkPlacer::new);
   });

   public StraightTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6) {
      setDirtAt(var1, var2, var3, var5.below(), var6);

      for(int var7 = 0; var7 < var4; ++var7) {
         this.placeLog(var1, var2, var3, var5.above(var7), var6);
      }

      return ImmutableList.of(new FoliagePlacer.FoliageAttachment(var5.above(var4), 0, false));
   }
}
