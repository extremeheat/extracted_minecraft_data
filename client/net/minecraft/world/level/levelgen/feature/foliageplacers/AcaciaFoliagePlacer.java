package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class AcaciaFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<AcaciaFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(var0 -> foliagePlacerParts(var0).apply(var0, AcaciaFoliagePlacer::new));

   public AcaciaFoliagePlacer(IntProvider var1, IntProvider var2) {
      super(var1, var2);
   }

   @Override
   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.ACACIA_FOLIAGE_PLACER;
   }

   @Override
   protected void createFoliage(
      LevelSimulatedReader var1,
      FoliagePlacer.FoliageSetter var2,
      RandomSource var3,
      TreeConfiguration var4,
      int var5,
      FoliagePlacer.FoliageAttachment var6,
      int var7,
      int var8,
      int var9
   ) {
      boolean var10 = var6.doubleTrunk();
      BlockPos var11 = var6.pos().above(var9);
      this.placeLeavesRow(var1, var2, var3, var4, var11, var8 + var6.radiusOffset(), -1 - var7, var10);
      this.placeLeavesRow(var1, var2, var3, var4, var11, var8 - 1, -var7, var10);
      this.placeLeavesRow(var1, var2, var3, var4, var11, var8 + var6.radiusOffset() - 1, 0, var10);
   }

   @Override
   public int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3) {
      return 0;
   }

   @Override
   protected boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var3 == 0 ? (var2 > 1 || var4 > 1) && var2 != 0 && var4 != 0 : var2 == var5 && var4 == var5 && var5 > 0;
   }
}
