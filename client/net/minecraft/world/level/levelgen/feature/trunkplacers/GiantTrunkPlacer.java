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

public class GiantTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<GiantTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return trunkPlacerParts(var0).apply(var0, GiantTrunkPlacer::new);
   });

   public GiantTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.GIANT_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6) {
      BlockPos var7 = var5.below();
      setDirtAt(var1, var2, var3, var7, var6);
      setDirtAt(var1, var2, var3, var7.east(), var6);
      setDirtAt(var1, var2, var3, var7.south(), var6);
      setDirtAt(var1, var2, var3, var7.south().east(), var6);
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for(int var9 = 0; var9 < var4; ++var9) {
         this.placeLogIfFreeWithOffset(var1, var2, var3, var8, var6, var5, 0, var9, 0);
         if (var9 < var4 - 1) {
            this.placeLogIfFreeWithOffset(var1, var2, var3, var8, var6, var5, 1, var9, 0);
            this.placeLogIfFreeWithOffset(var1, var2, var3, var8, var6, var5, 1, var9, 1);
            this.placeLogIfFreeWithOffset(var1, var2, var3, var8, var6, var5, 0, var9, 1);
         }
      }

      return ImmutableList.of(new FoliagePlacer.FoliageAttachment(var5.above(var4), 0, true));
   }

   private void placeLogIfFreeWithOffset(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, BlockPos.MutableBlockPos var4, TreeConfiguration var5, BlockPos var6, int var7, int var8, int var9) {
      var4.setWithOffset(var6, var7, var8, var9);
      this.placeLogIfFree(var1, var2, var3, var4, var5);
   }
}
