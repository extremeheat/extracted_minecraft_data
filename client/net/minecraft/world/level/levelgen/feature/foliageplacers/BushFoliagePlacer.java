package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class BushFoliagePlacer extends BlobFoliagePlacer {
   public static final MapCodec<BushFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return blobParts(var0).apply(var0, BushFoliagePlacer::new);
   });

   public BushFoliagePlacer(IntProvider var1, IntProvider var2, int var3) {
      super(var1, var2, var3);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.BUSH_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader var1, FoliagePlacer.FoliageSetter var2, RandomSource var3, TreeConfiguration var4, int var5, FoliagePlacer.FoliageAttachment var6, int var7, int var8, int var9) {
      for(int var10 = var9; var10 >= var9 - var7; --var10) {
         int var11 = var8 + var6.radiusOffset() - 1 - var10;
         this.placeLeavesRow(var1, var2, var3, var4, var6.pos(), var11, var10, var6.doubleTrunk());
      }

   }

   protected boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var2 == var5 && var4 == var5 && var1.nextInt(2) == 0;
   }
}
