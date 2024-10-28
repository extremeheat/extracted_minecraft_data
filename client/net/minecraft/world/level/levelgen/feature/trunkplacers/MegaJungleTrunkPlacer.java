package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class MegaJungleTrunkPlacer extends GiantTrunkPlacer {
   public static final MapCodec<MegaJungleTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return trunkPlacerParts(var0).apply(var0, MegaJungleTrunkPlacer::new);
   });

   public MegaJungleTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.MEGA_JUNGLE_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6) {
      ArrayList var7 = Lists.newArrayList();
      var7.addAll(super.placeTrunk(var1, var2, var3, var4, var5, var6));

      for(int var8 = var4 - 2 - var3.nextInt(4); var8 > var4 / 2; var8 -= 2 + var3.nextInt(4)) {
         float var9 = var3.nextFloat() * 6.2831855F;
         int var10 = 0;
         int var11 = 0;

         for(int var12 = 0; var12 < 5; ++var12) {
            var10 = (int)(1.5F + Mth.cos(var9) * (float)var12);
            var11 = (int)(1.5F + Mth.sin(var9) * (float)var12);
            BlockPos var13 = var5.offset(var10, var8 - 3 + var12 / 2, var11);
            this.placeLog(var1, var2, var3, var13, var6);
         }

         var7.add(new FoliagePlacer.FoliageAttachment(var5.offset(var10, var8, var11), -2, false));
      }

      return var7;
   }
}
