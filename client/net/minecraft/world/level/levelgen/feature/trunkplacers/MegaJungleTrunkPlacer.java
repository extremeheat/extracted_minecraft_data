package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class MegaJungleTrunkPlacer extends GiantTrunkPlacer {
   public static final Codec<MegaJungleTrunkPlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return trunkPlacerParts(var0).apply(var0, MegaJungleTrunkPlacer::new);
   });

   public MegaJungleTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.MEGA_JUNGLE_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BoundingBox var6, TreeConfiguration var7) {
      ArrayList var8 = Lists.newArrayList();
      var8.addAll(super.placeTrunk(var1, var2, var3, var4, var5, var6, var7));

      for(int var9 = var3 - 2 - var2.nextInt(4); var9 > var3 / 2; var9 -= 2 + var2.nextInt(4)) {
         float var10 = var2.nextFloat() * 6.2831855F;
         int var11 = 0;
         int var12 = 0;

         for(int var13 = 0; var13 < 5; ++var13) {
            var11 = (int)(1.5F + Mth.cos(var10) * (float)var13);
            var12 = (int)(1.5F + Mth.sin(var10) * (float)var13);
            BlockPos var14 = var4.offset(var11, var9 - 3 + var13 / 2, var12);
            placeLog(var1, var2, var14, var5, var6, var7);
         }

         var8.add(new FoliagePlacer.FoliageAttachment(var4.offset(var11, var9, var12), -2, false));
      }

      return var8;
   }
}
