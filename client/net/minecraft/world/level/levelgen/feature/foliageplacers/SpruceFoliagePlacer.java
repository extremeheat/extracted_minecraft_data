package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class SpruceFoliagePlacer extends FoliagePlacer {
   public static final MapCodec<SpruceFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return foliagePlacerParts(var0).and(IntProvider.codec(0, 24).fieldOf("trunk_height").forGetter((var0x) -> {
         return var0x.trunkHeight;
      })).apply(var0, SpruceFoliagePlacer::new);
   });
   private final IntProvider trunkHeight;

   public SpruceFoliagePlacer(IntProvider var1, IntProvider var2, IntProvider var3) {
      super(var1, var2);
      this.trunkHeight = var3;
   }

   protected FoliagePlacerType<?> type() {
      return FoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
   }

   protected void createFoliage(LevelSimulatedReader var1, FoliagePlacer.FoliageSetter var2, RandomSource var3, TreeConfiguration var4, int var5, FoliagePlacer.FoliageAttachment var6, int var7, int var8, int var9) {
      BlockPos var10 = var6.pos();
      int var11 = var3.nextInt(2);
      int var12 = 1;
      byte var13 = 0;

      for(int var14 = var9; var14 >= -var7; --var14) {
         this.placeLeavesRow(var1, var2, var3, var4, var10, var11, var14, var6.doubleTrunk());
         if (var11 >= var12) {
            var11 = var13;
            var13 = 1;
            var12 = Math.min(var12 + 1, var8 + var6.radiusOffset());
         } else {
            ++var11;
         }
      }

   }

   public int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3) {
      return Math.max(4, var2 - this.trunkHeight.sample(var1));
   }

   protected boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6) {
      return var2 == var5 && var4 == var5 && var5 > 0;
   }
}
