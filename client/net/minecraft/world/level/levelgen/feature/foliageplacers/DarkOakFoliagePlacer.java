package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class DarkOakFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<DarkOakFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return foliagePlacerParts(var0).apply(var0, DarkOakFoliagePlacer::new);
   });

   public DarkOakFoliagePlacer(IntProvider var1, IntProvider var2) {
      super(var1, var2);
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader var1, FoliagePlacer.FoliageSetter var2, RandomSource var3, TreeConfiguration var4, int var5, FoliagePlacer.FoliageAttachment var6, int var7, int var8, int var9) {
      BlockPos var10 = var6.pos().above(var9);
      boolean var11 = var6.doubleTrunk();
      if (var11) {
         this.placeLeavesRow(var1, var2, var3, var4, var10, var8 + 2, -1, var11);
         this.placeLeavesRow(var1, var2, var3, var4, var10, var8 + 3, 0, var11);
         this.placeLeavesRow(var1, var2, var3, var4, var10, var8 + 2, 1, var11);
         if (var3.nextBoolean()) {
            this.placeLeavesRow(var1, var2, var3, var4, var10, var8, 2, var11);
         }
      } else {
         this.placeLeavesRow(var1, var2, var3, var4, var10, var8 + 2, -1, var11);
         this.placeLeavesRow(var1, var2, var3, var4, var10, var8 + 1, 0, var11);
      }

   }

   public int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3) {
      return 4;
   }

   protected boolean shouldSkipLocationSigned(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var3 != 0 || !var6 || var2 != -var5 && var2 < var5 || var4 != -var5 && var4 < var5 ? super.shouldSkipLocationSigned(var1, var2, var3, var4, var5, var6) : true;
   }

   protected boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (var3 == -1 && !var6) {
         return var2 == var5 && var4 == var5;
      } else if (var3 == 1) {
         return var2 + var4 > var5 * 2 - 2;
      } else {
         return false;
      }
   }
}
