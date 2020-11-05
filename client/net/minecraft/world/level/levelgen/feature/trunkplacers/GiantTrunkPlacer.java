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

public class GiantTrunkPlacer extends TrunkPlacer {
   public static final Codec<GiantTrunkPlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return trunkPlacerParts(var0).apply(var0, GiantTrunkPlacer::new);
   });

   public GiantTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.GIANT_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BoundingBox var6, TreeConfiguration var7) {
      BlockPos var8 = var4.below();
      setDirtAt(var1, var8);
      setDirtAt(var1, var8.east());
      setDirtAt(var1, var8.south());
      setDirtAt(var1, var8.south().east());
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      for(int var10 = 0; var10 < var3; ++var10) {
         placeLogIfFreeWithOffset(var1, var2, var9, var5, var6, var7, var4, 0, var10, 0);
         if (var10 < var3 - 1) {
            placeLogIfFreeWithOffset(var1, var2, var9, var5, var6, var7, var4, 1, var10, 0);
            placeLogIfFreeWithOffset(var1, var2, var9, var5, var6, var7, var4, 1, var10, 1);
            placeLogIfFreeWithOffset(var1, var2, var9, var5, var6, var7, var4, 0, var10, 1);
         }
      }

      return ImmutableList.of(new FoliagePlacer.FoliageAttachment(var4.above(var3), 0, true));
   }

   private static void placeLogIfFreeWithOffset(LevelSimulatedRW var0, Random var1, BlockPos.MutableBlockPos var2, Set<BlockPos> var3, BoundingBox var4, TreeConfiguration var5, BlockPos var6, int var7, int var8, int var9) {
      var2.setWithOffset(var6, var7, var8, var9);
      placeLogIfFree(var0, var1, var2, var3, var4, var5);
   }
}
