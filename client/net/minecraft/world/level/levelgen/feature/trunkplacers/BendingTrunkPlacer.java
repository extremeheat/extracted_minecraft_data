package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class BendingTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<BendingTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return trunkPlacerParts(var0).and(var0.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("min_height_for_leaves", 1).forGetter((var0x) -> {
         return var0x.minHeightForLeaves;
      }), IntProvider.codec(1, 64).fieldOf("bend_length").forGetter((var0x) -> {
         return var0x.bendLength;
      }))).apply(var0, BendingTrunkPlacer::new);
   });
   private final int minHeightForLeaves;
   private final IntProvider bendLength;

   public BendingTrunkPlacer(int var1, int var2, int var3, int var4, IntProvider var5) {
      super(var1, var2, var3);
      this.minHeightForLeaves = var4;
      this.bendLength = var5;
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.BENDING_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6) {
      Direction var7 = Direction.Plane.HORIZONTAL.getRandomDirection(var3);
      int var8 = var4 - 1;
      BlockPos.MutableBlockPos var9 = var5.mutable();
      BlockPos var10 = var9.below();
      setDirtAt(var1, var2, var3, var10, var6);
      ArrayList var11 = Lists.newArrayList();

      int var12;
      for(var12 = 0; var12 <= var8; ++var12) {
         if (var12 + 1 >= var8 + var3.nextInt(2)) {
            var9.move(var7);
         }

         if (TreeFeature.validTreePos(var1, var9)) {
            this.placeLog(var1, var2, var3, var9, var6);
         }

         if (var12 >= this.minHeightForLeaves) {
            var11.add(new FoliagePlacer.FoliageAttachment(var9.immutable(), 0, false));
         }

         var9.move(Direction.UP);
      }

      var12 = this.bendLength.sample(var3);

      for(int var13 = 0; var13 <= var12; ++var13) {
         if (TreeFeature.validTreePos(var1, var9)) {
            this.placeLog(var1, var2, var3, var9, var6);
         }

         var11.add(new FoliagePlacer.FoliageAttachment(var9.immutable(), 0, false));
         var9.move(var7);
      }

      return var11;
   }
}
